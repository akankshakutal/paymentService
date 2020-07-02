package com.payment.paymentService.payment

import com.payment.paymentService.kafka.KafkaTopicProducer
import com.payment.paymentService.kafka.PaymentEvent
import com.payment.paymentService.payment.prospect.Prospect
import com.payment.paymentService.payment.prospect.ProspectRepository
import io.kotlintest.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class PaymentServiceTest {
    private val prospect = Prospect("orderId", PaymentMode.NET_BANKING, 2000, "PAID")
    private val prospectRepository = mockk<ProspectRepository> {
        every { findByOrderId(any()) } returns Mono.just(Prospect("orderId", PaymentMode.NET_BANKING, 2000, "PENDING"))
        every { save<Prospect>(any()) } returns Mono.just(prospect)
    }
    private val kafkaTopicProducer = mockk<KafkaTopicProducer>() {
        every { produce(any(), any(), any()) } returns Mono.empty()
    }
    private val paymentService = PaymentService(prospectRepository, kafkaTopicProducer)

    @BeforeEach
    fun setUp() {
        paymentService.topic ="topic"
    }

    @Test
    fun `should find and save order details to mongo for given ordrId`() {
        val paymentDetails = PaymentDetails("abcd1234", "john", 2000, "orderId", PaymentMode.NET_BANKING)

        val payment = paymentService.pay(paymentDetails)

        StepVerifier.withVirtualTime { payment }
                .consumeNextWith {
                    verify {
                        prospectRepository.findByOrderId("orderId")
                        prospectRepository.save(prospect)
                    }
                }
                .verifyComplete()
    }

    @Test
    fun `should return payment response`() {
        val paymentDetails = PaymentDetails("abcd1234", "john", 2000, "orderId", PaymentMode.NET_BANKING)

        val payment = paymentService.pay(paymentDetails).block()!!

        payment.status shouldBe "SUCCESS"
        payment.amount shouldBe 2000
    }

    @Test
    fun `should call kafka producer`() {
        val paymentDetails = PaymentDetails("abcd1234", "john", 2000, "orderId", PaymentMode.NET_BANKING)

        paymentService.pay(paymentDetails).block()

        verify { kafkaTopicProducer.produce(PaymentEvent("orderId", 2000, "PAID"), "topic", any()) }
    }
}