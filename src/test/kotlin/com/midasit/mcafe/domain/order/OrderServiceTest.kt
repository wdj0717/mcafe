package com.midasit.mcafe.domain.order

import com.midasit.mcafe.domain.member.MemberService
import com.midasit.mcafe.domain.order.dto.MenuCategoryDto
import com.midasit.mcafe.domain.order.dto.MenuInfoDto
import com.midasit.mcafe.domain.order.dto.OrderRequest
import com.midasit.mcafe.domain.room.RoomService
import com.midasit.mcafe.infra.component.UChefComponent
import com.midasit.mcafe.model.Member
import com.midasit.mcafe.model.Order
import com.midasit.mcafe.model.OrderStatus
import com.midasit.mcafe.model.Room
import com.midasit.mcafe.model.getRandomLong
import com.midasit.mcafe.model.getRandomSn
import com.midasit.mcafe.model.getRandomString
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify

class OrderServiceTest : BehaviorSpec({
    val uChefComponent = mockk<UChefComponent>(relaxed = true)
    val orderRepository = mockk<OrderRepository>(relaxed = true)
    val roomService = mockk<RoomService>(relaxed = true)
    val memberService = mockk<MemberService>(relaxed = true)
    val orderService = OrderService(uChefComponent, orderRepository, roomService, memberService)

    afterContainer {
        clearAllMocks()
    }

    given("주문 목록을 조회하려고 할때") {
        val menuCategoryDto = MenuCategoryDto(getRandomString(10), listOf())
        every { uChefComponent.getMenuList() } returns listOf(menuCategoryDto)
        When("주문 목록 조회 API를 호출하면") {
            val result = orderService.getMenuList()

            then("주문 목록이 반환된다.") {
                result.menuCategoryList shouldBe listOf(menuCategoryDto)
            }
        }
    }

    given("메뉴코드가 주어지면") {
        val menuCode = getRandomString(10)
        val menuInfoDto =
            MenuInfoDto(getRandomString(10), menuCode, getRandomLong(1000), getRandomLong(1000), arrayListOf())
        every { uChefComponent.getMenuInfo(any()) } returns menuInfoDto
        When("메뉴 정보 조회 API를 호출하면") {
            val result = orderService.getMenuInfo(menuCode)

            then("메뉴 정보가 반환된다.") {
                result.menuInfo shouldBe menuInfoDto
            }
        }
    }

    given("멤버 Sn과 방 Sn, 메뉴코드가 주어지면") {
        val memberSn = getRandomSn()
        val roomSn = getRandomSn()
        val menuCode = getRandomString(10)
        val request =
            OrderRequest.Create(menuCode, roomSn, listOf(getRandomLong(10), getRandomLong(10), getRandomLong(10)))
        val member = Member(memberSn = memberSn)
        val room = Room(roomSn = roomSn)
        val quantity = 1L
        val order = Order(OrderStatus.PENDING, menuCode, member, room, quantity)
        request.optionList.forEach {
            order.addOption(it)
        }
        every { memberService.findBySn(any()) } answers { member }
        every { roomService.findBySn(any()) } answers { room }
        every { roomService.checkMemberInRoom(any(), any()) } just Runs
        every {
            orderRepository.findByMemberAndRoomAndMenuCodeAndStatus(
                any(),
                any(),
                any(),
                any()
            )
        } answers { listOf() }
        every { orderRepository.save(any()) } answers { order }
        every { uChefComponent.getMenuInfo(any()) } answers {
            MenuInfoDto(
                getRandomString(10),
                menuCode,
                getRandomLong(1000),
                getRandomLong(1000),
                arrayListOf()
            )
        }
        When("신규 주문 등록하면") {
            val result = orderService.createOrder(memberSn, request)

            then("주문 등록 결과가 반환된다.") {
                result.memberSn shouldBe memberSn
                result.roomSn shouldBe roomSn
                result.optionList shouldBe request.optionList
            }
        }

        every { orderRepository.findByMemberAndRoomAndMenuCodeAndStatus(any(), any(), any(), any()) } answers {
            listOf(
                order
            )
        }
        When("기존 주문이 있으면") {
            val result = orderService.createOrder(memberSn, request)

            then("기존 주문의 수량이 증가한다.") {
                result.quantity shouldBe quantity + 1
            }
        }
    }

    given("멤버 Sn과 주문 Sn, 수량이 주어지면") {
        val memberSn = getRandomSn()
        val orderSn = getRandomSn()
        val quantity = getRandomLong(100)
        val member = Member(memberSn = memberSn)
        val order = Order(quantity = quantity, member = member)
        every { memberService.findBySn(any()) } answers { member }
        every { orderRepository.getOrThrow(any()) } answers { order }
        When("주문 수량을 변경하면") {
            val result = orderService.updateOrderQuantity(memberSn, orderSn, quantity)

            then("주문 수량이 변경된다.") {
                result shouldBe true
                order.quantity shouldBe quantity
            }
        }
    }

    given("멤버 Sn과 방 Sn, 주문 Sn이 주어지면") {
        val memberSn = getRandomSn()
        val roomSn = getRandomSn()
        val orderSn = getRandomSn()
        val member = Member(memberSn = memberSn)
        val room = Room(roomSn = roomSn)
        val order = Order(member = member, room = room)
        every { memberService.findBySn(any()) } answers { member }
        every { roomService.findBySn(any()) } answers { room }
        every { roomService.checkMemberInRoom(any(), any()) } just Runs
        every { orderRepository.getOrThrow(any()) } answers { order }
        every { orderRepository.delete(any()) } just Runs
        When("주문을 삭제하면") {
            val result = orderService.deleteOrder(memberSn, roomSn, orderSn)

            then("주문이 삭제된다.") {
                result shouldBe true
                verify(exactly = 1) { orderRepository.delete(any()) }
            }
        }
    }
})