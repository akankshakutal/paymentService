package com.payment.paymentService.kafka

import com.payment.paymentService.payment.PaymentMode
import com.payment.paymentService.payment.prospect.Prospect
import com.payment.paymentService.payment.prospect.ProspectRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kafka.receiver.KafkaReceiver

class KafkaConsumerTest {
    private val prospectRepository = mockk<ProspectRepository>()
    private val kafkaReceiver = mockk<KafkaReceiver<PartitionIdentifier, Event>> {
        every { receive() } returns Flux.empty()
    }

    @Test
    fun `should return the result`() {
        val kafkaTopicConsumer = KafkaConsumer(kafkaReceiver, prospectRepository)

        kafkaTopicConsumer.run(null)

        verify(exactly = 1) { kafkaReceiver.receive() }
    }

    @Test
    fun `should save order details to mongo `() {
        val prospect = Prospect("orderId", PaymentMode.NET_BANKING, 2000, "PENDING")
        every { prospectRepository.save<Prospect>(any()) } returns Mono.just(prospect)

        val kafkaTopicConsumer = KafkaConsumer(kafkaReceiver, prospectRepository)

        kafkaTopicConsumer.process(Event("orderId", PaymentMode.NET_BANKING, 2000))

        verify(exactly = 1) { prospectRepository.save(prospect) }
    }
}