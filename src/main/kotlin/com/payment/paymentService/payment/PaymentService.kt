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
        return prospectRepository.save(Prospect(paymentDetails.orderId, paymentDetails.paymentMode, paymentDetails.amount))
                .map { PaymentResponse("SUCCESS") }
    }
}

class PaymentResponse(val status: String)
