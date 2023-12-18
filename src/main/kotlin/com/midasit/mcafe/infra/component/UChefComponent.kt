package com.midasit.mcafe.infra.component

import com.fasterxml.jackson.databind.ObjectMapper
import com.midasit.mcafe.domain.order.Order
import com.midasit.mcafe.domain.order.dto.*
import com.midasit.mcafe.infra.component.rq.uchef.payorder.OrderRq
import com.midasit.mcafe.infra.component.rs.uchef.login.UChefLoginRs
import com.midasit.mcafe.infra.component.rs.uchef.menu.ListRow
import com.midasit.mcafe.infra.component.rs.uchef.menu.UChefMenuRs
import com.midasit.mcafe.infra.component.rs.uchef.menuinfo.OptionGroup
import com.midasit.mcafe.infra.component.rs.uchef.menuinfo.UChefMenuInfoRs
import com.midasit.mcafe.infra.component.rs.uchef.payorder.UChefPayOrderRs
import com.midasit.mcafe.infra.component.rs.uchef.projectseq.UChefProjectSeqRs
import com.midasit.mcafe.infra.component.rs.uchef.securityid.UChefSecurityIdRs
import com.midasit.mcafe.infra.exception.CustomException
import com.midasit.mcafe.infra.exception.ErrorMessage
import com.midasit.mcafe.model.validate
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
        validate(ErrorMessage.INVALID_UCHEF_AUTH) { getSecurityId(phone) == securityId }
        val res = getUChefClient(uChefLogIn, shopMemberSeq, phone, password, getProjectSeq())
        val uChefLogInRs = res.toReadValue(UChefLoginRs::class.java)

        validate(ErrorMessage.INVALID_UCHEF_AUTH) { uChefLogInRs.resultCode == "0" }

        val uuid = UUID.randomUUID().toString()
        val valueOperations = redisTemplate.opsForValue()
        valueOperations.set(uuid, phone, Duration.ofMinutes(30))

        return uuid
    }

    fun getMenuList(): List<MenuCategoryDto> {
        val res = getUChefClient(uChefMenuPath, shopMemberSeq, getProjectSeq())
        val uChefMenuRs = res.toReadValue(UChefMenuRs::class.java)
        return uChefMenuRs.parseMenuList()
    }

    fun payOrder(nickname: String, phone: String, orderList: List<Order>): String {
        val orderRqList = orderList.makeOrderRqList()
        val orderListParam: String =
            objectMapper.writeValueAsString(orderRqList) ?: throw CustomException(ErrorMessage.INTERNAL_SERVER_ERROR)
        val couponAmount: Long = calculatePrice(orderRqList)

        val res = getUChefClient(
            uChefPayOrderPath,
            orderListParam,
            nickname,
            phone,
            couponAmount,
            shopMemberSeq,
            getProjectSeq()
        )
        val uChefPayOrderRs = res.toReadValue(UChefPayOrderRs::class.java)

        require(uChefPayOrderRs.resultCode == "0") { throw CustomException(ErrorMessage.UCHEF_ORDER_FAILED) }

        return uChefPayOrderRs.searchResult.orderNo
    }

    private fun calculatePrice(orderListParam: List<OrderRq>): Long {
        return orderListParam.sumOf { it.couponAmount * it.menuQty }
    }

    private fun requestMenuInfo(menuCode: String): MenuInfoDto {
        val res = getUChefClient(uChefMenuInfoPath, menuCode, shopMemberSeq)
        val uChefMenuInfoRs = objectMapper.readValue(res, UChefMenuInfoRs::class.java)
        val menuInfoDto = uChefMenuInfoRs.parseMenuInfo()
        menuMap[menuCode] = menuInfoDto

        return menuInfoDto
    }

    private fun getProjectSeq(): Int {
        val res = getUChefClient(uChefProjectSeqPath, shopMemberSeq)
        val uChefProjectSeqRs = res.toReadValue(UChefProjectSeqRs::class.java)
        return uChefProjectSeqRs.searchResult.memberData.defaultProjectSeq
    }

    private fun getSecurityId(phone: String): String {
        val res = getUChefClient(uChefGetSecurityId, shopMemberSeq, phone, getProjectSeq())
        val uChefSecurityIdRs = res.toReadValue(UChefSecurityIdRs::class.java)

        return uChefSecurityIdRs.searchResult?.securityId ?: throw CustomException(ErrorMessage.INVALID_UCHEF_AUTH)
    }

    private fun createUChefClient(): WebClient {
        return webClient.mutate().baseUrl(uChefDomain).build()
    }

    private fun getUChefClient(path: String, vararg params: Any): String? {
        return createUChefClient()
            .get()
            .uri(path, *params)
            .retrieve()
            .bodyToMono(String::class.java)
            .block()
    }

    private fun UChefMenuRs.parseMenuList(): List<MenuCategoryDto> {
        return searchResult.jsonData.pageList.page.map { page ->
            val menuCategoryName = page.name
            val menuList = page.listComp.listRow.flatMap {
                it.toMenuDto()
            }
            MenuCategoryDto(menuCategoryName, menuList)
        }
    }

    private fun ListRow.toMenuDto() = orderButtonComp.map {
        MenuDto.from(it)
    }

    private fun UChefMenuInfoRs.parseMenuInfo(): MenuInfoDto {
        val menuInfo = searchResult.menuInfoList[0]
        val option = menuInfo.optionGroupList.map {
            OptionGroupDto.of(it, it.getOptionDtoList())
        }
        return MenuInfoDto.of(menuInfo, option)
    }

    private fun OptionGroup.getOptionDtoList(): List<OptionDto> {
        return optionList.map { OptionDto.from(it) }
    }

    private fun List<Order>.makeOrderRqList(): List<OrderRq> {
        return map {
            OrderRq.of(it, getMenuInfo(it.menuCode))
        }
    }

    private fun <T> String?.toReadValue(clazz: Class<T>): T {
        return objectMapper.readValue(this, clazz)
    }
}
