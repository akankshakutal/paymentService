package com.payment.paymentService.kafka

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.header.Headers
import org.apache.kafka.common.serialization.Deserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.test.context.ActiveProfiles
import reactor.core.publisher.Mono
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverOptions
import reactor.util.context.Context
import java.util.concurrent.CountDownLatch

@Component
@ActiveProfiles("test")
class TestKafkaConsumer : ApplicationRunner {
    @Value("\${spring.kafka.bootstrap-servers}")
    lateinit var kafkaUrl: String

    @Value("\${spring.kafka.template.default-topic}")
    lateinit var topic: String

    var countDownLatch = CountDownLatch(1)
    var messageList = mutableListOf<PaymentEvent>()

    override fun run(args: ApplicationArguments?) {
        getKafkaReceiver().receive()
                .flatMapSequential { receiverRecord ->
                    process(receiverRecord.value() as PaymentEvent)
                            .subscriberContext(getContextFromKafkaHeader(receiverRecord.headers()))
                            .flatMap { Mono.just(receiverRecord.receiverOffset()) }
                }
                .flatMap { it.commit() }
                .subscribe()
    }

    private fun getContextFromKafkaHeader(headers: Headers): Context {
        var context = Context.empty()
        val httpHeaders = HttpHeaders()

        when {
            headers.count() != 0 -> {
                headers.iterator().forEach { httpHeaders[it.key()] = listOf(String(it.value())) }
                context = context.put("headers", httpHeaders)
            }
        }
        return context
    }

    private fun process(message: PaymentEvent): Mono<Boolean> {
        return Mono.subscriberContext().map {
            messageList.add(message)
            countDownLatch.countDown()
        }.map { true }
    }

    private fun getKafkaReceiver(): KafkaReceiver<String, ByteArray> {
        val properties = mutableMapOf<String, Any>(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaUrl,
                ConsumerConfig.CLIENT_ID_CONFIG to "payment",
                ConsumerConfig.GROUP_ID_CONFIG to "payment-event-group-id",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to PartitionIdDeserializer::class.java,
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to PaymentEventDeserializer::class.java
        )
        val receiverOptions = ReceiverOptions.create<String, ByteArray>(properties)
                .subscription(listOf(topic))

        return KafkaReceiver.create(receiverOptions)
    }
}

class PaymentEventDeserializer : Deserializer<PaymentEvent> {

    override fun deserialize(topic: String?, data: ByteArray): PaymentEvent {
        return jacksonObjectMapper().readValue<PaymentEvent>(data)
    }

}

class PartitionIdDeserializer : Deserializer<PartitionIdentifier> {
    override fun deserialize(topic: String?, data: ByteArray): PartitionIdentifier {
        return jacksonObjectMapper().readValue(data)
    }

}
