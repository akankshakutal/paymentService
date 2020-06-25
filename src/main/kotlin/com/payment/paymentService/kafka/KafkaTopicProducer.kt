package com.payment.paymentService.kafka

import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderRecord
import java.util.*

@Component
class KafkaTopicProducer(val kafkaSender: KafkaSender<PartitionIdentifier, PaymentEvent>) {

    fun produce(data: PaymentEvent, topicName: String, partitionIdentifier: String): Mono<Boolean> {
        return Mono.subscriberContext().flatMap {
            val headers1 = createProducerRecordWithHeaders(data, topicName, partitionIdentifier)
            val senderRecord = SenderRecord.create(headers1, UUID.randomUUID().toString())
            kafkaSender.send(Mono.just(senderRecord)).next()
        }.map { true }.onErrorReturn(false)
    }

    private fun createProducerRecordWithHeaders(data: PaymentEvent, topicName: String, partitionIdentifier: String)
            : ProducerRecord<PartitionIdentifier, PaymentEvent> {
        return when {
            partitionIdentifier.isBlank() -> ProducerRecord(topicName, data)
            else -> ProducerRecord(topicName, PartitionIdentifier(partitionIdentifier), data)
        }
    }
}

