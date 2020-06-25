package com.payment.paymentService.kafka

import io.kotlintest.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderRecord

class KafkaTopicProducerTest {

    private val kafkaSender = mockk<KafkaSender<PartitionIdentifier, PaymentEvent>> {
        every { send(any<Mono<SenderRecord<PartitionIdentifier, PaymentEvent, String>>>()) } returns Flux.empty()
    }

    @Test
    fun `should return the result`() {
        val slot = slot<Mono<SenderRecord<PartitionIdentifier, PaymentEvent, String>>>()
        val paymentEvent = PaymentEvent("orderId1234", 1000, "PAID")
        val kafkaTopicProducer = KafkaTopicProducer(kafkaSender)

        kafkaTopicProducer.produce(paymentEvent, "topic", "abcd1234").block()

        verify { kafkaSender.send(capture(slot)) }

        val record = slot.captured.block()!!

        record.topic() shouldBe "topic"
        record.value() shouldBe paymentEvent
    }
}