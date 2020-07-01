package com.payment.paymentService.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.payment.paymentService.payment.PaymentMode
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serializer

data class Event(val orderId: String, val paymentMode: PaymentMode, val amount: Int)

class EventDeserializer : Deserializer<Event> {
    override fun deserialize(topic: String?, data: ByteArray): Event = jacksonObjectMapper().readValue(data)
}

data class PartitionIdentifier(val id: String)

class PartitionIdDeserializer : Deserializer<PartitionIdentifier> {
    override fun deserialize(topic: String?, data: ByteArray): PartitionIdentifier = jacksonObjectMapper().readValue(data)
}

data class PaymentEvent(val orderId: String, val amount: Int, val status: String)

class PaymentEventSerializer<T> : Serializer<T> {
    override fun serialize(topic: String?, data: T?): ByteArray = jacksonObjectMapper().writeValueAsBytes(data)
}

class PartitionIdSerializer<PartitionIdentifier> : Serializer<PartitionIdentifier> {
    override fun serialize(topic: String?, data: PartitionIdentifier?): ByteArray = ObjectMapper().writeValueAsBytes(data)
}