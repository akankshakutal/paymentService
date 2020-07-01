package com.payment.paymentService.kafka

import com.payment.paymentService.payment.PaymentMode
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.concurrent.TimeUnit

@ActiveProfiles("test")
@SpringBootTest
@ExtendWith(SpringExtension::class)
@EmbeddedKafka(controlledShutdown = true, brokerProperties = ["log.dir=out/embedded-kafka/paymentService"])
class KafkaConsumerIntegrationTest(@Autowired val testKafkaProducer: TestKafkaProducer) {
    @Autowired
    private lateinit var kafkaConsumer: KafkaConsumer

    @Test
    fun `should consumer kafka events`() {
        kafkaConsumer.setCountDownLatch(1)
        val event = Event("orderId",PaymentMode.NET_BANKING,2000)

        testKafkaProducer.produce(event, "orderDetails", "abcd1234").subscribe()

        kafkaConsumer.countDownLatch.await(5, TimeUnit.SECONDS)
        val receivedMessages = kafkaConsumer.messageList

        receivedMessages.size shouldBe 1

    }
}