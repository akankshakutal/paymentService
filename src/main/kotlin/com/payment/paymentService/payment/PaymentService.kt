package com.payment.paymentService.payment

import com.payment.paymentService.payment.prospect.Prospect
import com.payment.paymentService.payment.prospect.ProspectRepository
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class PaymentService(
        val prospectRepository: ProspectRepository
) {
    fun pay(paymentDetails: PaymentDetails): Mono<PaymentResponse> {
        return prospectRepository.findByOrderId(paymentDetails.orderId).flatMap {
            prospect ->
            prospect.status = "PAID"
            prospectRepository.save(prospect)
        }.map { PaymentResponse("SUCCESS", it.amount) }
    }
}

class PaymentResponse(val status: String, val amount: Int)
