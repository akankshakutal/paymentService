package com.payment.paymentService.kafka

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.kafka.common.serialization.Deserializer

class EventDeserializer : Deserializer<Event> {

    override fun deserialize(topic: String?, data: ByteArray): Event {
        return jacksonObjectMapper().readValue(data)
    }

}

class PartitionIdDeserializer : Deserializer<PartitionIdentifier> {
    override fun deserialize(topic: String?, data: ByteArray): PartitionIdentifier {
        return jacksonObjectMapper().readValue(data)
    }
}

data class Event(
        val partitionIdentifier: String,
        val itemName: String,
        val quantity: Int,
        val paymentMode: String,
        val email: String
)

data class PartitionIdentifier(val id: String)
