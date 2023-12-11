package com.midasit.mcafe.domain.favoritemenu

import com.midasit.mcafe.domain.favoritemenu.dto.FavoriteMenuDto
import com.midasit.mcafe.domain.favoritemenu.dto.FavoriteMenuRequest
import com.midasit.mcafe.domain.favoritemenu.dto.FavoriteMenuResponse
import com.midasit.mcafe.model.BaseController
import io.swagger.v3.oas.annotations.Operation
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/favorite")
class FavoriteMenuController(val favoriteMenuService: FavoriteMenuService) : BaseController {

    @Operation(summary = "즐겨찾기 메뉴 조회")
    @GetMapping
    fun findFavoriteMenu(authentication: Authentication): FavoriteMenuResponse.Results {
        return favoriteMenuService.findFavoriteMenu(getMemberSn(authentication)).toResult()
    }

    @Operation(summary = "즐겨찾기 메뉴 추가")
    @PostMapping
    fun createFavoriteMenu(
        @RequestBody request: FavoriteMenuRequest.Create,
        authentication: Authentication
    ): FavoriteMenuResponse.Result {
        return favoriteMenuService.createFavoriteMenu(getMemberSn(authentication), request.menuCode).toResult()
    }

    @Operation(summary = "즐겨찾기 메뉴 삭제")
    @DeleteMapping("/{favoriteSn}")
    fun deleteFavoriteMenu(
        @PathVariable favoriteSn: Long,
        authentication: Authentication
    ) {
        favoriteMenuService.deleteFavoriteMenu(getMemberSn(authentication), favoriteSn)
    }


    private fun List<FavoriteMenuDto>.toResult(): FavoriteMenuResponse.Results {
        return FavoriteMenuResponse.Results.from(this)
    }

    private fun FavoriteMenuDto.toResult(): FavoriteMenuResponse.Result {
        return FavoriteMenuResponse.Result.from(this)
    }
}