package com.midasit.mcafe.domain.favoritemenu

import com.midasit.mcafe.domain.favoritemenu.dto.FavoriteMenuDto
import com.midasit.mcafe.domain.favoritemenu.dto.FavoriteMenuRequest
import com.midasit.mcafe.domain.favoritemenu.dto.FavoriteMenuResponse
import com.midasit.mcafe.model.ControllerTest
import io.kotest.matchers.collections.shouldExist
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.mockito.InjectMocks
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
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

        given("memberSn과 menuCode가 주어졌을 경우") {
            val memberSn = 1L
            val request = FavoriteMenuRequest.Create("test")
            val favoriteMenuDto = FavoriteMenuDto(1L, request.menuCode, memberSn)
            every { favoriteMenuService.createFavoriteMenu(any(), any()) } answers { favoriteMenuDto }
            When("해당 member의 즐겨찾기 메뉴를 추가하면") {
                val response = perform(
                    post("/favorite")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON_VALUE)
                ).andExpect { status().isOk }.andReturn()
                Then("추가한 즐겨찾기 메뉴가 반환된다.") {
                    val result = getObject(response.response.contentAsString, FavoriteMenuResponse.Result::class.java)
                    result.menuCode shouldBe favoriteMenuDto.menuCode
                }
            }
        }

        given("menuCode가 주어지면") {
            val favoriteSn = 2L
            every { favoriteMenuService.deleteFavoriteMenu(any(), any()) } just Runs
            When("해당 member의 즐겨찾기 메뉴를 삭제 API를 호출하면") {
                perform(
                    delete("/favorite/$favoriteSn")
                ).andExpect { status().isOk }.andReturn()
                Then("삭제 로직이 수행된다.") {
                    verify(exactly = 1) { favoriteMenuService.deleteFavoriteMenu(any(), any()) }
                }
            }
        }
    }
}