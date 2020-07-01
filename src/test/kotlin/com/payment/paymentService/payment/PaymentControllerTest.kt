package com.payment.paymentService.payment

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.payment.paymentService.utils.any
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono

@WebFluxTest(controllers = [PaymentController::class])
class PaymentControllerTest(@Autowired val testClient: WebTestClient) {

    @MockBean
    private lateinit var paymentService: PaymentService

    @Test
    fun `should save order details and return it with 200 ok`() {
        val response = PaymentResponse("SUCCESS")
        Mockito.`when`(paymentService.pay(any())).thenReturn(Mono.just(response))
        testClient.post()
                .uri("/make/payment")
                .bodyValue(PaymentDetails("KOTAK1234", "display name", 3000, "orderId", PaymentMode.NET_BANKING))
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .json(jacksonObjectMapper().writeValueAsString(response))
    }
}