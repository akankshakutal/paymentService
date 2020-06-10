package com.payment.paymentService.payment

import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class PaymentService {
    fun pay(accountDetails: AccountDetails): Mono<PaymentResponse> {
        return Mono.empty()
    }
}

class PaymentResponse
