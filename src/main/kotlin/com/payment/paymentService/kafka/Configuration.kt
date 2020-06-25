package com.payment.paymentService.kafka

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverOptions
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderOptions
import java.util.*

@Configuration
class Configuration {
    @Bean
    fun kafkaReceiver(kafkaConfig: KafkaConfig): KafkaReceiver<PartitionIdentifier, Event> {
        val properties = mutableMapOf<String, Any>(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaConfig.kafkaUrl,
                ConsumerConfig.CLIENT_ID_CONFIG to kafkaConfig.clientId,
                ConsumerConfig.GROUP_ID_CONFIG to kafkaConfig.groupId,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to PartitionIdDeserializer::class.java,
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to kafkaConfig.ackConfig,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to EventDeserializer::class.java
        )
        val receiverOptions = ReceiverOptions.create<PartitionIdentifier, Event>(properties)
                .subscription(listOf(kafkaConfig.topic))

        return KafkaReceiver.create(receiverOptions)
    }

    @Bean
    fun kafkaSender(kafkaConfig: KafkaConfig): KafkaSender<PartitionIdentifier, PaymentEvent>? {
        val props = HashMap<String, Any>()
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaConfig.kafkaUrl
        props[ProducerConfig.CLIENT_ID_CONFIG] = kafkaConfig.clientId
        props[ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION] = 1
        props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = kafkaConfig.ackConfig
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = PartitionIdSerializer::class.java
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = PaymentEventSerializer::class.java

        val senderOptions = SenderOptions.create<PartitionIdentifier, PaymentEvent>(props)
        return KafkaSender.create(senderOptions)
    }
}