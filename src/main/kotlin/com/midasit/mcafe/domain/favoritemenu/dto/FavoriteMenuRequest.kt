package com.midasit.mcafe.domain.favoritemenu.dto

import io.swagger.v3.oas.annotations.media.Schema

class FavoriteMenuRequest {

    @Schema(name = "FavoriteMenuRequestCreate", description = "즐겨찾기 메뉴 생성")
    class Create(val menuCode: String)
}
