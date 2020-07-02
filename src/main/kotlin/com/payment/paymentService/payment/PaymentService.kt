package com.payment.paymentService.payment

import com.payment.paymentService.kafka.KafkaTopicProducer
import com.payment.paymentService.kafka.PaymentEvent
import com.payment.paymentService.payment.prospect.ProspectRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.util.*

@Component
class PaymentService(
        val prospectRepository: ProspectRepository,
        val kafkaTopicProducer: KafkaTopicProducer
) {
    @Value("\${spring.kafka.producer.properties.topic}")
    lateinit var topic: String

    fun pay(paymentDetails: PaymentDetails): Mono<PaymentResponse> {
        return prospectRepository.findByOrderId(paymentDetails.orderId).flatMap { prospect ->
            prospect.status = "PAID"
            prospectRepository.save(prospect)
        }.map {
            kafkaTopicProducer.produce(PaymentEvent(it.orderId, it.amount, it.status), topic, UUID.randomUUID().toString())
                    .subscribeOn(Schedulers.elastic())
                    .subscribe()
            PaymentResponse("SUCCESS", it.amount)
        }
    }
}

class PaymentResponse(val status: String, val amount: Int)
