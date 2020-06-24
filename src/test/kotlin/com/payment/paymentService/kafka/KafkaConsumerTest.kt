package com.payment.paymentService.kafka

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.kafka.receiver.KafkaReceiver

class KafkaConsumerTest {
    private val kafkaReceiver = mockk<KafkaReceiver<PartitionIdentifier, Event>> {
        every { receive() } returns Flux.empty()
    }

    @Test
    fun `should return the result`() {
        val kafkaTopicConsumer = KafkaConsumer(kafkaReceiver)

        kafkaTopicConsumer.run(null)

        verify(exactly = 1) { kafkaReceiver.receive() }
    }
}