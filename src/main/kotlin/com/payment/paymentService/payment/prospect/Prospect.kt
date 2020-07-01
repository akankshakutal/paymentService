package com.payment.paymentService.payment.prospect

import com.payment.paymentService.payment.PaymentMode
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "paymentDetails")
data class Prospect(val orderId: String, val paymentMode: PaymentMode, val amount: Int) {
    @Id
    lateinit var id: String
}