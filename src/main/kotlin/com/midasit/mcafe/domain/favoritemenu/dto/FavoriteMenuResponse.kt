package com.midasit.mcafe.domain.favoritemenu.dto

import io.swagger.v3.oas.annotations.media.Schema

class FavoriteMenuResponse {

    @Schema(name = "FavoriteMenuResponseResult", description = "즐겨찾기 메뉴 조회 결과")
    class Result(
        val sn: Long,
        val menuCode: String,
        val memberSn: Long,
    ) {
        companion object {
            fun from(favoriteMenu: FavoriteMenuDto): Result {
                return Result(favoriteMenu.sn, favoriteMenu.menuCode, favoriteMenu.memberSn)
            }
        }
    }

    @Schema(name = "FavoriteMenuResponseResults", description = "즐겨찾기 메뉴 조회 결과 리스트")
    class Results(
        val results: List<Result>
    ) {
        companion object {
            fun from(favoriteMenus: List<FavoriteMenuDto>): Results {
                return Results(favoriteMenus.map { Result.from(it) })
            }
        }
    }
}