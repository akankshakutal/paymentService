package com.payment.paymentService.payment.prospect

import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface ProspectRepository : ReactiveMongoRepository<Prospect, String>