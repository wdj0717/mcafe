package com.midasit.mcafe.infra.component

import com.fasterxml.jackson.databind.ObjectMapper
import com.midasit.mcafe.domain.order.dto.*
import com.midasit.mcafe.infra.component.rs.uchef.login.UChefLoginRs
import com.midasit.mcafe.infra.component.rs.uchef.menu.UChefMenuRs
import com.midasit.mcafe.infra.component.rs.uchef.menuinfo.UChefMenuInfoRs
import com.midasit.mcafe.infra.component.rs.uchef.projectSeq.UChefProjectSeqRs
import com.midasit.mcafe.infra.component.rs.uchef.securityid.UChefSecurityIdRs
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class UChefComponent(private val webClient: WebClient,
                     private val objectMapper: ObjectMapper,
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
                     private val uChefMenuInfoPath: String) {

    private fun createUChefClient(): WebClient {
        return webClient.mutate().baseUrl(uChefDomain).build()
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

    fun login(phone: String, securityId: String, password: String): Boolean {
        if (getSecurityId(phone) != securityId) {
            return false
        }
        val res = createUChefClient()
                .get()
                .uri(uChefLogIn, shopMemberSeq, phone, password, getProjectSeq())
                .retrieve()
                .bodyToMono(String::class.java)
                .block()
        val uChefLogInRs= objectMapper.readValue(res, UChefLoginRs::class.java)

        return uChefLogInRs.resultCode == "0"
    }

    private fun getSecurityId(phone: String): String {
        val res = createUChefClient()
                .get()
                .uri(uChefGetSecurityId, shopMemberSeq, phone, getProjectSeq())
                .retrieve()
                .bodyToMono(String::class.java)
                .block()

        val uChefSecurityIdRs = objectMapper.readValue(res, UChefSecurityIdRs::class.java)

        return uChefSecurityIdRs.searchResult?.securityId ?: throw Exception("존재하지 않습니다.")
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

    fun getMenuInfo(menuCode: Long): MenuInfoDto {
        val res = createUChefClient()
                .get()
                .uri(uChefMenuInfoPath, menuCode, shopMemberSeq)
                .retrieve()
                .bodyToMono(String::class.java)
                .block()
        val uChefMenuInfoRs = objectMapper.readValue(res, UChefMenuInfoRs::class.java)

        return parseMenuInfo(uChefMenuInfoRs)
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

}
