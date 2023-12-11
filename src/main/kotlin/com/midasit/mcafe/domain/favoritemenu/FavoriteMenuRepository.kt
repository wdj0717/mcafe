package com.midasit.mcafe.domain.favoritemenu

import com.midasit.mcafe.domain.member.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.findByIdOrNull


fun FavoriteMenuRepository.getOrThrow(sn: Long): FavoriteMenu =
    findByIdOrNull(sn) ?: throw IllegalArgumentException("해당 즐겨찾기 메뉴가 존재하지 않습니다.")

interface FavoriteMenuRepository : JpaRepository<FavoriteMenu, Long> {

    fun findByMember(member: Member): List<FavoriteMenu>
}