package com.payment.paymentService.payment

data class PaymentDetails(val accountNumber: String, val name: String, val amount: Int, val orderId: String, val paymentMode: PaymentMode)

enum class PaymentMode {
    NET_BANKING,
    CASH_ON_DELIVERY,
    UPI
}