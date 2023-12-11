package com.midasit.mcafe.domain.favoritemenu.dto

import com.midasit.mcafe.domain.favoritemenu.FavoriteMenu

data class FavoriteMenuDto(val sn: Long, val menuCode: String, val memberSn: Long) {
    companion object {
        fun from(favoriteMenu: FavoriteMenu): FavoriteMenuDto {
            return FavoriteMenuDto(favoriteMenu.sn, favoriteMenu.menuCode, favoriteMenu.member.sn)
        }
    }
}