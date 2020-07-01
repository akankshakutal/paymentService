package com.payment.paymentService.payment

import com.payment.paymentService.payment.prospect.Prospect
import com.payment.paymentService.payment.prospect.ProspectRepository
import io.kotlintest.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class PaymentServiceTest {
    private val prospectRepository = mockk<ProspectRepository>()
    private val paymentService = PaymentService(prospectRepository)

    @Test
    fun `should save order details to mongo`() {
        val prospect = Prospect("orderId", PaymentMode.NET_BANKING, 2000)
        every { prospectRepository.save<Prospect>(any()) } returns Mono.just(prospect)
        val paymentDetails = PaymentDetails("abcd1234", "john", 2000, "orderId", PaymentMode.NET_BANKING)

        val payment = paymentService.pay(paymentDetails)

        StepVerifier.withVirtualTime { payment }
                .consumeNextWith { verify { prospectRepository.save(prospect) } }
                .verifyComplete()
    }

    @Test
    fun `should return payment response`() {
        val prospect = Prospect("orderId", PaymentMode.NET_BANKING, 2000)
        every { prospectRepository.save<Prospect>(any()) } returns Mono.just(prospect)
        val paymentDetails = PaymentDetails("abcd1234", "john", 2000, "orderId", PaymentMode.NET_BANKING)

        val payment = paymentService.pay(paymentDetails).block()!!
        payment.status shouldBe "SUCCESS"


    }

}