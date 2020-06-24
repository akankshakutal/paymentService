package com.payment.paymentService.kafka

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class KafkaConfig {

    @Value("\${spring.kafka.bootstrap-servers}")
    lateinit var kafkaUrl: String

    @Value("\${spring.kafka.listener.ack-mode}")
    lateinit var ackConfig: String

    @Value("\${spring.kafka.consumer.client-id}")
    lateinit var clientId: String

    @Value("\${spring.kafka.template.default-topic}")
    lateinit var topic: String

    @Value("\${spring.kafka.consumer.group-id}")
    lateinit var groupId: String
}
