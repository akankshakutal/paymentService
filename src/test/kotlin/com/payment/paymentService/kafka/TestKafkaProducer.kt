package com.payment.paymentService.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.Serializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.test.context.ActiveProfiles
import reactor.core.publisher.Mono
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderOptions
import reactor.kafka.sender.SenderRecord
import java.util.*

@Component
@ActiveProfiles("test")
class TestKafkaProducer {

    @Value("\${spring.kafka.bootstrap-servers}")
    lateinit var kafkaUrl: String

    @Value("\${spring.kafka.admin.client-id}")
    lateinit var clientId: String

    fun produce(data: Event, topicName: String, partitionIdentifier: String): Mono<Boolean> {
        return Mono.subscriberContext().flatMap {
            val headers1 = createProducerRecordWithHeaders(data, topicName, partitionIdentifier)
            val senderRecord = SenderRecord.create(headers1, UUID.randomUUID().toString())
            getKafkaSender().send(Mono.just(senderRecord)).next()
        }.map { true }.onErrorReturn(false)
    }

    private fun createProducerRecordWithHeaders(data: Event, topicName: String, partitionIdentifier: String)
            : ProducerRecord<PartitionIdentifier, Event> {
        return when {
            partitionIdentifier.isBlank() -> ProducerRecord(topicName, data)
            else -> ProducerRecord(topicName, PartitionIdentifier(partitionIdentifier), data)
        }
    }

    fun getKafkaSender(): KafkaSender<PartitionIdentifier, Event> {
        val props = HashMap<String, Any>()
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaUrl
        props[ProducerConfig.CLIENT_ID_CONFIG] = clientId
        props[ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION] = 1
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = PartitionIdSerializer::class.java
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = EventSerializer::class.java

        val senderOptions = SenderOptions.create<PartitionIdentifier, Event>(props)
        return KafkaSender.create(senderOptions)
    }
}

class EventSerializer<T> : Serializer<T> {
    override fun serialize(topic: String?, data: T?): ByteArray {
        return ObjectMapper().writeValueAsBytes(data)
    }
}

class PartitionIdSerializer<PartitionIdentifier> : Serializer<PartitionIdentifier> {
    override fun serialize(topic: String?, data: PartitionIdentifier?): ByteArray {
        return ObjectMapper().writeValueAsBytes(data)
    }
}
