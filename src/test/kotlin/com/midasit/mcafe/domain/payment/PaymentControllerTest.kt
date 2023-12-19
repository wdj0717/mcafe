package com.midasit.mcafe.domain.payment

import com.midasit.mcafe.domain.payment.dto.PaymentRequest
import com.midasit.mcafe.domain.payment.dto.PaymentResponse
import com.midasit.mcafe.model.ControllerTest
import com.midasit.mcafe.model.getRandomLong
import com.midasit.mcafe.model.getRandomSn
import com.midasit.mcafe.model.getRandomString
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.mockito.InjectMocks
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class PaymentControllerTest : ControllerTest() {

    private val paymentService: PaymentService = mockk()

    @InjectMocks
    private val paymentController = PaymentController(paymentService)
    override fun getController(): Any {
        return paymentController
    }

    init {
        afterContainer {
            clearAllMocks()
        }

        given("음료 주문정보가 주어지면") {
            val request = PaymentRequest.PayOrder(getRandomSn(), listOf(getRandomLong(100)))
            val paymentRs = PaymentResponse.PayOrder(getRandomString(100), listOf())
            every { paymentService.payOrder(any(), any(), any()) } answers { paymentRs }
            When("음료 주문 API를 호출하면") {
                val response = perform(
                    post("/payment")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                ).andExpect { status().isOk }.andReturn()
                Then("추가한 즐겨찾기 메뉴가 반환된다.") {
                    val result = getObject(response.response.contentAsString, PaymentResponse.PayOrder::class.java)
                    result.orderNo shouldBe paymentRs.orderNo
                }
            }
        }
    }
}