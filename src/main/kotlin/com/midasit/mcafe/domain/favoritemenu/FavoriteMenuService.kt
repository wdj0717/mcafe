package com.midasit.mcafe.domain.favoritemenu

import com.midasit.mcafe.domain.favoritemenu.dto.FavoriteMenuDto
import com.midasit.mcafe.domain.member.MemberService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FavoriteMenuService(
    val favoriteMenuRepository: FavoriteMenuRepository,
    val memberService: MemberService,
) {
    fun findFavoriteMenu(memberSn: Long): List<FavoriteMenuDto> {
        val member = memberService.findBySn(memberSn)
        return favoriteMenuRepository.findByMember(member).map { FavoriteMenuDto.from(it) }
    }
}
