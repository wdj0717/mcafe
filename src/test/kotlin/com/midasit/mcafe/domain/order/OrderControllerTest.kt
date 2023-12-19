package com.midasit.mcafe.domain.order

import com.midasit.mcafe.domain.order.dto.MenuCategoryDto
import com.midasit.mcafe.domain.order.dto.MenuInfoDto
import com.midasit.mcafe.domain.order.dto.OrderDto
import com.midasit.mcafe.domain.order.dto.OrderRequest
import com.midasit.mcafe.domain.order.dto.OrderResponse
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class OrderControllerTest : ControllerTest() {

    private val orderService: OrderService = mockk()

    @InjectMocks
    private val orderController = OrderController(orderService)

    override fun getController(): Any {
        return orderController
    }

    init {
        afterContainer {
            clearAllMocks()
        }

        given("메뉴 목록을 조회하려고 할때") {
            val menuCategoryDto = MenuCategoryDto(getRandomString(10), listOf())
            every { orderService.getMenuList() } answers { OrderResponse.GetMenuList(listOf(menuCategoryDto)) }
            When("메뉴 목록 조회 API를 호출하면") {
                val result = perform(
                    get("/order/menu")
                ).andExpect {
                    status().isOk
                }.andReturn()
                then("메뉴 목록이 반환된다.") {
                    val res = getObject(result.response.contentAsString, OrderResponse.GetMenuList::class.java)
                    res.menuCategoryList shouldBe listOf(menuCategoryDto)
                }
            }
        }

        given("메뉴 코드가 주어지면") {
            val menuCode = getRandomString(10)
            val menuInfoDto =
                MenuInfoDto(getRandomString(10), menuCode, getRandomLong(1000), getRandomLong(1000), arrayListOf())
            every { orderService.getMenuInfo(any()) } answers { OrderResponse.GetMenuInfo(menuInfoDto) }
            When("메뉴 정보 조회 API를 호출하면") {
                val result = perform(
                    get("/order/menu/$menuCode")
                ).andExpect {
                    status().isOk
                }.andReturn()
                then("메뉴 정보가 반환된다.") {
                    val res = getObject(result.response.contentAsString, OrderResponse.GetMenuInfo::class.java)
                    res.menuInfo shouldBe menuInfoDto
                }
            }
        }

        given("방번호와 reqeust가 주어지면") {
            val roomSn = getRandomSn()
            val request =
                OrderRequest.Create(getRandomString(10), roomSn, listOf(getRandomLong(100), getRandomLong(100)))
            val menuInfoDto = MenuInfoDto(
                getRandomString(10),
                request.menuCode,
                getRandomLong(1000),
                getRandomLong(1000),
                arrayListOf()
            )
            val orderDto = OrderDto(
                getRandomSn(),
                getRandomSn(),
                getRandomString(10),
                roomSn, menuInfoDto, 1L, request.optionList
            )
            every { orderService.createOrder(any(), any()) } answers { orderDto }
            When("주문 생성 API를 호출하면") {
                val result = perform(
                    post("/order")
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

        given("주문 번호와 수량이 주어지면") {
            val orderSn = getRandomSn()
            val quantity = getRandomLong(100)
            every { orderService.updateOrderQuantity(any(), any(), any()) } answers { true }
            When("주문 수량 변경 API를 호출하면") {
                val result = perform(
                    patch("/order/$orderSn")
                        .content(objectMapper.writeValueAsString(OrderRequest.UpdateQuantity(quantity)))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                ).andExpect {
                    status().isOk
                }.andReturn()
                then("주문 수량 변경 결과가 반환된다.") {
                    val res = getObject(result.response.contentAsString, Boolean::class.java)
                    res shouldBe true
                }
            }
        }

        given("주문 번호와 방번호가 주어지면") {
            val orderSn = getRandomSn()
            val roomSn = getRandomSn()
            every { orderService.deleteOrder(any(), any(), any()) } answers { true }
            When("주문 삭제 API를 호출하면") {
                val result = perform(
                    delete("/order/$orderSn")
                        .param("roomSn", roomSn.toString())
                ).andExpect {
                    status().isOk
                }.andReturn()
                then("주문 삭제 결과가 반환된다.") {
                    val res = getObject(result.response.contentAsString, Boolean::class.java)
                    res shouldBe true
                }
            }
        }
    }
}