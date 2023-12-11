package com.midasit.mcafe.domain.favoritemenu

import com.midasit.mcafe.domain.favoritemenu.dto.FavoriteMenuDto
import com.midasit.mcafe.domain.member.MemberService
import com.midasit.mcafe.infra.exception.ErrorMessage
import com.midasit.mcafe.model.validate
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
        return favoriteMenuRepository.findByMember(member).map { it.toDto() }
    }

    @Transactional
    fun createFavoriteMenu(memberSn: Long, menuCode: String): FavoriteMenuDto {
        val member = memberService.findBySn(memberSn)
        val favoriteMenu = FavoriteMenu(menuCode, member)
        return favoriteMenuRepository.save(favoriteMenu).toDto()
    }


    private fun FavoriteMenu.toDto(): FavoriteMenuDto {
        return FavoriteMenuDto.from(this)
    }

    @Transactional
    fun deleteFavoriteMenu(memberSn: Long, favoriteSn: Long) {
        val member = memberService.findBySn(memberSn)
        val favoriteMenu = favoriteMenuRepository.getOrThrow(favoriteSn)
        validate(ErrorMessage.INVALID_MEMBER) { favoriteMenu.member.sn == member.sn }
        favoriteMenuRepository.delete(favoriteMenu)
    }
}
