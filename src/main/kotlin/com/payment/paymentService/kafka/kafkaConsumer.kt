package com.payment.paymentService.kafka

import org.apache.kafka.common.header.Headers
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import reactor.kafka.receiver.KafkaReceiver
import reactor.util.context.Context
import java.util.concurrent.CountDownLatch

@Component
class KafkaConsumer(val kafkaReceiver: KafkaReceiver<PartitionIdentifier, Event>) : ApplicationRunner {
    var countDownLatch = CountDownLatch(1)
    var messageList = mutableListOf<Event>()

    override fun run(args: ApplicationArguments?) {
        kafkaReceiver.receive()
                .flatMapSequential { receiverRecord ->
                    process(receiverRecord.value() as Event)
                            .subscriberContext(getContextFromKafkaHeader(receiverRecord.headers()))
                            .flatMap { receiverRecord.receiverOffset().toMono() }
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

    private fun process(message: Event): Mono<Boolean> {
        return Mono.subscriberContext().map {
            messageList.add(message)
            countDownLatch.countDown()
        }.map { true }
    }

}
