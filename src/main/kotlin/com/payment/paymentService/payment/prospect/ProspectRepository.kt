package com.payment.paymentService.payment.prospect

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface ProspectRepository : ReactiveMongoRepository<Prospect, String> {
    fun findByOrderId(orderId: String) : Mono<Prospect>
}