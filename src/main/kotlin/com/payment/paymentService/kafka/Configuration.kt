package com.payment.paymentService.kafka

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverOptions

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
}