package com.payment.paymentService.payment

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class PaymentController(val paymentService: PaymentService) {

    @PostMapping("/make/payment")
    fun pay(@RequestBody accountDetails: AccountDetails): Mono<PaymentResponse> {
        return paymentService.pay(accountDetails)
    }

}