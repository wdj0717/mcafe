package com.midasit.mcafe.domain.favoritemenu

import com.midasit.mcafe.domain.member.Member
import com.midasit.mcafe.infra.exception.ErrorMessage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.findByIdOrNull


fun FavoriteMenuRepository.getOrThrow(sn: Long): FavoriteMenu =
    findByIdOrNull(sn) ?: throw IllegalArgumentException(ErrorMessage.INVALID_FAVORITE_MENU.message)

interface FavoriteMenuRepository : JpaRepository<FavoriteMenu, Long> {

    fun findByMember(member: Member): List<FavoriteMenu>
}