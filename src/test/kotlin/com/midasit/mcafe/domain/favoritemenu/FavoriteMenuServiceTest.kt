package com.midasit.mcafe.domain.favoritemenu

import com.midasit.mcafe.domain.favoritemenu.dto.FavoriteMenuDto
import com.midasit.mcafe.domain.member.MemberService
import com.midasit.mcafe.infra.exception.CustomException
import com.midasit.mcafe.model.Member
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.repository.findByIdOrNull

class FavoriteMenuServiceTest : BehaviorSpec({

    val memberService = mockk<MemberService>()
    val favoriteMenuRepository = mockk<FavoriteMenuRepository>()
    val favoriteMenuService = FavoriteMenuService(favoriteMenuRepository, memberService)

    afterContainer {
        clearAllMocks()
    }

    given("member Sn이 주어지면") {
        val memberSn = 1L
        val member = Member()
        val favoriteMenu = FavoriteMenu("test", member)
        every { memberService.findBySn(any()) } answers { member }
        every { favoriteMenuRepository.findByMember(any()) } answers { listOf(favoriteMenu) }
        When("해당 멤버의 즐겨찾기 메뉴를 조회하면") {
            val result = favoriteMenuService.findFavoriteMenu(memberSn)
            then("해당 멤버의 즐겨찾기 메뉴를 반환한다") {
                result[0] shouldBe FavoriteMenuDto.from(favoriteMenu)
            }
        }
    }

    given("memberSn과 menuCode가 주어지면") {
        val memberSn = 1L
        val menuCode = "test"
        val member = Member()
        val favoriteMenu = FavoriteMenu(menuCode, member)
        every { memberService.findBySn(any()) } answers { member }
        every { favoriteMenuRepository.save(any()) } answers { favoriteMenu }
        When("해당 멤버의 즐겨찾기 메뉴를 추가하면") {
            val result = favoriteMenuService.createFavoriteMenu(memberSn, menuCode)
            then("추가한 즐겨찾기 메뉴를 반환한다") {
                result shouldBe FavoriteMenuDto.from(favoriteMenu)
            }
        }
    }

    given("memberSn과 Favorite Sn이 주어지면") {
        val memberSn = 1L
        val favoriteSn = 2L
        val member = Member()
        val favoriteMenu = FavoriteMenu("test", member)

        every { memberService.findBySn(any()) } answers { member }
        every { favoriteMenuRepository.findByIdOrNull(any()) } answers { favoriteMenu }
        every { favoriteMenuRepository.delete(any()) } just Runs
        When("해당 멤버의 즐겨찾기 메뉴를 삭제하면") {
            favoriteMenuService.deleteFavoriteMenu(memberSn, favoriteSn)
            then("삭제가 된다.") {
                verify(exactly = 1) { favoriteMenuRepository.delete(any()) }
            }
        }

        every { memberService.findBySn(any()) } answers { member }
        every { favoriteMenuRepository.findByIdOrNull(any()) } answers { null }
        When("해당 즐겨찾기가 없으면") {
            then("에러가 발생한다.") {
                shouldThrow<IllegalArgumentException> {
                    favoriteMenuService.deleteFavoriteMenu(memberSn, favoriteSn)
                }
            }
        }

        every { memberService.findBySn(any()) } answers { member }
        every { favoriteMenuRepository.findByIdOrNull(any()) } answers { FavoriteMenu("test", Member(2L)) }
        When("해당 즐겨찾기가 다른 멤버의 것이면") {
            then("에러가 발생한다.") {
                shouldThrow<CustomException> {
                    favoriteMenuService.deleteFavoriteMenu(memberSn, favoriteSn)
                }
            }
        }
    }
})
