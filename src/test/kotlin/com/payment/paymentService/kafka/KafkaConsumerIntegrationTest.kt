package com.payment.paymentService.kafka

import io.kotlintest.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

@ActiveProfiles("test")
@SpringBootTest
@ExtendWith(SpringExtension::class)
@EmbeddedKafka(controlledShutdown = true, brokerProperties = ["log.dir=out/embedded-kafka/paymentService"])
class KafkaConsumerIntegrationTest(@Autowired val testKafkaProducer: TestKafkaProducer) {
    @Autowired
    private lateinit var kafkaConsumer: KafkaConsumer

    @Test
    fun `should consumer kafka events`() {
        val event = Event("Id", "itemName", 3, "NET_BANKING", "email")

        testKafkaProducer.produce(event, "orderDetails", "abcd1234").subscribe()

        val receivedMessages = kafkaConsumer.messageList

        Thread.sleep(200)
        receivedMessages.size shouldBe 1

    }
}