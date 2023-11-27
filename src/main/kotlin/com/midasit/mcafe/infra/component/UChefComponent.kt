package com.midasit.mcafe.infra.component

import com.fasterxml.jackson.databind.ObjectMapper
import com.midasit.mcafe.domain.member.Member
import com.midasit.mcafe.domain.order.Order
import com.midasit.mcafe.domain.order.dto.*
import com.midasit.mcafe.infra.component.rq.uchef.payorder.OrderRq
import com.midasit.mcafe.infra.component.rs.uchef.login.UChefLoginRs
import com.midasit.mcafe.infra.component.rs.uchef.menu.UChefMenuRs
import com.midasit.mcafe.infra.component.rs.uchef.menuinfo.UChefMenuInfoRs
import com.midasit.mcafe.infra.component.rs.uchef.payorder.UChefPayOrderRs
import com.midasit.mcafe.infra.component.rs.uchef.projectseq.UChefProjectSeqRs
import com.midasit.mcafe.infra.component.rs.uchef.securityid.UChefSecurityIdRs
import com.midasit.mcafe.infra.exception.CustomException
import com.midasit.mcafe.infra.exception.ErrorMessage
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.time.Duration
import java.util.*

@Component
class UChefComponent(
    private val webClient: WebClient,
    private val objectMapper: ObjectMapper,
    private val redisTemplate: RedisTemplate<String, Any>,
    @Value("\${u-chef.shop-member-seq}")
    private val shopMemberSeq: Int,
    @Value("\${u-chef.domain}")
    private val uChefDomain: String,
    @Value("\${u-chef.path.project-seq}")
    private val uChefProjectSeqPath: String,
    @Value("\${u-chef.path.security-id}")
    private val uChefGetSecurityId: String,
    @Value("\${u-chef.path.login}")
    private val uChefLogIn: String,
    @Value("\${u-chef.path.menu}")
    private val uChefMenuPath: String,
    @Value("\${u-chef.path.menu-info}")
    private val uChefMenuInfoPath: String,
    @Value("\${u-chef.path.pay-order}")
    private val uChefPayOrderPath: String
) {
    private var menuMap = HashMap<String, MenuInfoDto>()

    fun getMenuInfo(menuCode: String): MenuInfoDto {
        return menuMap[menuCode] ?: requestMenuInfo(menuCode)
    }

    fun login(phone: String, securityId: String, password: String): String {
        if (getSecurityId(phone) != securityId) {
            throw CustomException(ErrorMessage.INVALID_UCHEF_AUTH)
        }
        val res = createUChefClient()
            .get()
            .uri(uChefLogIn, shopMemberSeq, phone, password, getProjectSeq())
            .retrieve()
            .bodyToMono(String::class.java)
            .block()
        val uChefLogInRs = objectMapper.readValue(res, UChefLoginRs::class.java)

        if (uChefLogInRs.resultCode == "0") {
            val uuid = UUID.randomUUID().toString()
            val valueOperations = redisTemplate.opsForValue()
            valueOperations.set(uuid, phone, Duration.ofMinutes(30))

            return uuid
        } else {
            throw CustomException(ErrorMessage.INVALID_UCHEF_AUTH)
        }
    }

    fun getMenuList(): ArrayList<MenuCategoryDto> {
        val res = createUChefClient()
            .get()
            .uri(uChefMenuPath, shopMemberSeq, getProjectSeq())
            .retrieve()
            .bodyToMono(String::class.java)
            .block()
        val uChefMenuRs = objectMapper.readValue(res, UChefMenuRs::class.java)

        return parseMenuList(uChefMenuRs)
    }

    fun payOrder(member: Member, orderList: List<Order>): String {
        val csName: String = member.nickname
        val phone: String = member.phone
        val orderRqList = makeOrderRqList(orderList)
        val orderListParam: String =
            objectMapper.writeValueAsString(orderRqList) ?: throw CustomException(ErrorMessage.INTERNAL_SERVER_ERROR)
        val couponAmount: Long = calculatePrice(orderRqList)

        val res = createUChefClient()
            .get()
            .uri(uChefPayOrderPath, orderListParam, csName, phone, couponAmount, shopMemberSeq, getProjectSeq())
            .retrieve()
            .bodyToMono(String::class.java)
            .block()
        val uChefPayOrderRs = objectMapper.readValue(res, UChefPayOrderRs::class.java)

        if (uChefPayOrderRs.resultCode == "0") {
            return uChefPayOrderRs.searchResult.orderNo
        } else {
            throw CustomException(ErrorMessage.UCHEF_ORDER_FAILED)
        }
    }

    private fun calculatePrice(orderListParam: List<OrderRq>): Long {
        return orderListParam.sumOf { it.couponAmount * it.menuQty }
    }

    private fun createUChefClient(): WebClient {
        return webClient.mutate().baseUrl(uChefDomain).build()
    }

    private fun requestMenuInfo(menuCode: String): MenuInfoDto {
        val res = createUChefClient()
            .get()
            .uri(uChefMenuInfoPath, menuCode, shopMemberSeq)
            .retrieve()
            .bodyToMono(String::class.java)
            .block()
        val uChefMenuInfoRs = objectMapper.readValue(res, UChefMenuInfoRs::class.java)
        val menuInfoDto = parseMenuInfo(uChefMenuInfoRs)
        menuMap[menuCode] = menuInfoDto

        return menuInfoDto
    }

    private fun getProjectSeq(): Int {
        val res = createUChefClient()
            .get()
            .uri(uChefProjectSeqPath, shopMemberSeq)
            .retrieve()
            .bodyToMono(String::class.java)
            .block()
        val uChefProjectSeqRs = objectMapper.readValue(res, UChefProjectSeqRs::class.java)

        return uChefProjectSeqRs.searchResult.memberData.defaultProjectSeq
    }

    private fun getSecurityId(phone: String): String {
        val res = createUChefClient()
            .get()
            .uri(uChefGetSecurityId, shopMemberSeq, phone, getProjectSeq())
            .retrieve()
            .bodyToMono(String::class.java)
            .block()

        val uChefSecurityIdRs = objectMapper.readValue(res, UChefSecurityIdRs::class.java)

        return uChefSecurityIdRs.searchResult?.securityId ?: throw CustomException(ErrorMessage.INVALID_UCHEF_AUTH)
    }

    private fun parseMenuList(uChefMenuRs: UChefMenuRs): ArrayList<MenuCategoryDto> {
        val result = arrayListOf<MenuCategoryDto>()

        uChefMenuRs.searchResult.jsonData.pageList.page.forEach { page ->
            val menuCategoryName = page.name
            val menuList = arrayListOf<MenuDto>()
            page.listComp.listRow.forEach { row ->
                row.orderButtonComp.forEach { menu ->
                    val (menuCode, menuUnit, menuPrice, menuName, menuStock) = menu
                    val menuDto = MenuDto(menuName, menuCode, menuPrice, menuUnit, menuStock)
                    menuList.add(menuDto)
                }
            }
            val menuCategoryDto = MenuCategoryDto(menuCategoryName, menuList)
            result.add(menuCategoryDto)
        }

        return result
    }

    private fun parseMenuInfo(uChefMenuInfoRs: UChefMenuInfoRs): MenuInfoDto {
        val optionGroupDtoList = arrayListOf<OptionGroupDto>()

        val menuInfo = uChefMenuInfoRs.searchResult.menuInfoList[0]
        val (menuCode, menuName, menuPrice, menuStock, optionGroupList) = menuInfo
        optionGroupList.forEach { optionGroup ->
            val optionDtoList = arrayListOf<OptionDto>()
            val (optionGroupName, selectMin, selectMax, optionList) = optionGroup
            optionList.forEach { option ->
                val (optionName, optionCode, optionPrice, optionDefault) = option
                val optionDto = OptionDto(optionName, optionCode, optionPrice, optionDefault)
                optionDtoList.add(optionDto)
            }

            val optionGroupDto = OptionGroupDto(optionGroupName, selectMin, selectMax, optionDtoList)
            optionGroupDtoList.add(optionGroupDto)
        }

        return MenuInfoDto(menuName, menuCode, menuPrice, menuStock, optionGroupDtoList)
    }

    private fun makeOrderRqList(orderList: List<Order>): List<OrderRq> {
        val orderRqList: List<OrderRq> = orderList.map {
            OrderRq.of(it, getMenuInfo(it.menuCode))
        }
        return orderRqList
    }
}
