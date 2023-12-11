package com.midasit.mcafe.domain.favoritemenu

import com.midasit.mcafe.domain.favoritemenu.dto.FavoriteMenuDto
import com.midasit.mcafe.domain.favoritemenu.dto.FavoriteMenuResponse
import com.midasit.mcafe.model.ControllerTest
import io.kotest.matchers.collections.shouldExist
import io.mockk.every
import io.mockk.mockk
import org.mockito.InjectMocks
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class FavoriteControllerTest : ControllerTest() {

    private val favoriteMenuService: FavoriteMenuService = mockk()

    @InjectMocks
    private val favoriteMenuController = FavoriteMenuController(favoriteMenuService)
    override fun getController(): Any {
        return favoriteMenuController
    }

    init {
        given("member Sn이 주어졌을 경우") {
            val memberSn = 1L
            val favoriteMenuDto = FavoriteMenuDto(1L, "test", memberSn)
            every { favoriteMenuService.findFavoriteMenu(any()) } answers { listOf(favoriteMenuDto) }
            When("해당 member의 즐겨찾기 메뉴 API를 조회하면") {
                val response = perform(
                    get("/favorite")
                ).andExpect { status().isOk }.andReturn()
                Then("즐겨찾기 메뉴 목록이 반환된다.") {
                    val result = getObject(response.response.contentAsString, FavoriteMenuResponse.Results::class.java)
                    result.results.shouldExist { it.menuCode == favoriteMenuDto.menuCode }
                }
            }
        }
    }
}