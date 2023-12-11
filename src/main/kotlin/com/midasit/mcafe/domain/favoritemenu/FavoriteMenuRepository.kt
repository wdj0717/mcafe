package com.midasit.mcafe.domain.favoritemenu

import com.midasit.mcafe.domain.member.Member
import org.springframework.data.jpa.repository.JpaRepository

interface FavoriteMenuRepository : JpaRepository<FavoriteMenu, Long> {

    fun findByMember(member: Member): List<FavoriteMenu>
}