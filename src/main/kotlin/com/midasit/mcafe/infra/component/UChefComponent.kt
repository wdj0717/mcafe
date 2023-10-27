package com.midasit.mcafe.infra.component

import com.fasterxml.jackson.databind.ObjectMapper
import com.midasit.mcafe.domain.order.dto.MenuCategoryDto
import com.midasit.mcafe.domain.order.dto.MenuDto
import com.midasit.mcafe.infra.component.rs.uchef.menu.UChefMenuRs
import com.midasit.mcafe.infra.component.rs.uchef.projectSeq.UChefProjectSeqRs
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class UChefComponent(private val webClient: WebClient,
                     private val objectMapper: ObjectMapper,
                     @Value("\${u-chef.shop-member-seq}")
                     private val memberSeq: Int,
                     @Value("\${u-chef.domain}")
                     private val uChefDomain: String,
                     @Value("\${u-chef.path.project-seq}")
                     private val uChefProjectSeqPath: String,
                     @Value("\${u-chef.path.menu}")
                     private val uChefMenuPath: String) {

    private fun createUChefClient(): WebClient {
        return webClient.mutate().baseUrl(uChefDomain).build()
    }

    private fun getProjectSeq(): Int {
        val res = createUChefClient()
                .get()
                .uri(uChefProjectSeqPath, memberSeq)
                .retrieve()
                .bodyToMono(String::class.java)
                .block()
        val uChefProjectSeqRs = objectMapper.readValue(res, UChefProjectSeqRs::class.java)

        return uChefProjectSeqRs.searchResult.memberData.defaultProjectSeq
    }

    fun getMenuList(): ArrayList<MenuCategoryDto> {
        val res = createUChefClient()
                .get()
                .uri(uChefMenuPath, memberSeq, getProjectSeq())
                .retrieve()
                .bodyToMono(String::class.java)
                .block()
        val uChefMenuRs = objectMapper.readValue(res, UChefMenuRs::class.java)

        return parseMenuList(uChefMenuRs)
    }

    private fun parseMenuList(uChefMenuRs: UChefMenuRs): ArrayList<MenuCategoryDto> {
        val result = arrayListOf<MenuCategoryDto>()

        uChefMenuRs.searchResult.jsonData.pageList.page.forEach { page ->
            val name = page.name
            val menuList = arrayListOf<MenuDto>()
            page.listComp.listRow.forEach { row ->
                row.orderButtonComp.forEach { menu ->
                    val menuDto = MenuDto(menu.name, menu.itemCode, menu.price, menu.unit, menu.stock)
                    menuList.add(menuDto)
                }
            }
            val menuCategoryDto = MenuCategoryDto(name, menuList)
            result.add(menuCategoryDto)
        }

        return result
    }
}
