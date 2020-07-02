package com.payment.paymentService.prospect

import com.payment.paymentService.payment.PaymentMode
import com.payment.paymentService.payment.prospect.Prospect
import com.payment.paymentService.payment.prospect.ProspectRepository
import io.kotlintest.shouldBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@DataMongoTest
class ProspectRepositoryTest {
    @Autowired
    lateinit var prospectRepository: ProspectRepository

    @AfterEach
    internal fun tearDown() {
        prospectRepository.deleteAll().block()
    }

    @Test
    fun `save prospect`() {
        val prospect = Prospect("orderId", PaymentMode.NET_BANKING, 3000, "PENDING")
        prospectRepository.save(prospect).block()

        val savedProspect = prospectRepository.findAll().blockFirst()

        savedProspect shouldBe prospect
    }

    @Test
    fun `should find document by orderId`() {
        val prospect = Prospect("orderId", PaymentMode.NET_BANKING, 3000, "PENDING")
        prospectRepository.save(prospect).block()

        val savedProspect = prospectRepository.findByOrderId("orderId").block()

        savedProspect shouldBe prospect
    }
}
