package com.midasit.mcafe.domain.order

import com.midasit.mcafe.domain.order.dto.MenuInfoDto
import com.midasit.mcafe.domain.order.dto.OrderDto
import com.midasit.mcafe.domain.order.dto.OrderRequest
import com.midasit.mcafe.domain.order.dto.OrderResponse
import com.midasit.mcafe.model.ControllerTest
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.mockito.InjectMocks
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class OrderControllerTest: ControllerTest() {

    private val orderService: OrderService = mockk()

    @InjectMocks
    private val orderController =  OrderController(orderService)

    override fun getController(): Any {
        return orderController
    }

    init {
        afterContainer {
            clearAllMocks()
        }

        given("방번호와 reqeust가 주어지면") {
            val roomSn = 1L
            val request = OrderRequest.Create("menuCode", roomSn, listOf(1L, 2L))
            val menuInfoDto = MenuInfoDto("name", request.menuCode, 1000L, 1000L, arrayListOf())
            val orderDto = OrderDto(1L,
                "nickname",
                roomSn, menuInfoDto, 1L, request.optionList)
            every { orderService.createOrder(any(), any()) } returns orderDto
            When("주문 생성 API를 호출하면") {
                val result = perform(post("/order")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                ).andExpect {
                    status().isOk
                }.andReturn()
                then("주문 생성 결과가 반환된다.") {
                    val res = getObject(result.response.contentAsString, OrderResponse.Create::class.java)
                    res.orderDto shouldBe orderDto
                }
            }
        }
    }
}