package com.midasit.mcafe.domain.favoritemenu

import com.midasit.mcafe.domain.favoritemenu.dto.FavoriteMenuDto
import com.midasit.mcafe.domain.favoritemenu.dto.FavoriteMenuResponse
import com.midasit.mcafe.model.BaseController
import io.swagger.v3.oas.annotations.Operation
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
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


    private fun List<FavoriteMenuDto>.toResult(): FavoriteMenuResponse.Results {
        return FavoriteMenuResponse.Results.from(this)
    }
}