package com.midasit.mcafe.domain.member

import com.midasit.mcafe.domain.member.dto.MemberDto
import com.midasit.mcafe.domain.member.dto.MemberRequest
import com.midasit.mcafe.domain.roommember.RoomMemberRepository
import com.midasit.mcafe.infra.component.UChefComponent
import com.midasit.mcafe.infra.config.jwt.JwtTokenProvider
import com.midasit.mcafe.infra.exception.CustomException
import com.midasit.mcafe.infra.exception.ErrorMessage
import com.midasit.mcafe.model.Member
import com.midasit.mcafe.model.PasswordEncryptUtil
import com.midasit.mcafe.model.Role
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.test.util.ReflectionTestUtils

class MemberServiceTest : BehaviorSpec({
    val memberRepository = mockk<MemberRepository>(relaxed = true)
    val jwtTokenProvider = mockk<JwtTokenProvider>(relaxed = true)
    val uChefComponent = mockk<UChefComponent>(relaxed = true)
    val redisTemplate = mockk<RedisTemplate<String, Any>>(relaxed = true)
    val roomMemberRepository = mockk<RoomMemberRepository>(relaxed = true)
    val memberService = MemberService(
        uChefComponent = uChefComponent,
        memberRepository = memberRepository,
        jwtTokenProvider = jwtTokenProvider,
        redisTemplate = redisTemplate,
        roomMemberRepository = roomMemberRepository)

    afterContainer {
        clearAllMocks()
    }

    given("회원가입을 위한 정보를 받아온다.") {
        val request = MemberRequest.Signup("username", "1q2w3e4r5t", "1q2w3e4r5t", "name", "certKey")
        val member = Member(
            phone = "010-1234-5678",
            username = request.username,
            nickname = request.nickname,
            password = request.password,
            role = Role.USER
        )
        ReflectionTestUtils.setField(member, "sn", 1L)
        every { memberRepository.save(any()) } returns member
        `when`("회원가입을 요청한다.") {
            val result = memberService.signup(request)
            then("회원가입이 완료된다.") {
                result.nickname shouldBe member.nickname
                result.phone shouldBe member.phone
            }
        }
    }

    given("로그인 정보를 받아온다.") {
        var request = MemberRequest.Login("username", "1q2w3e4r5t")
        val member = Member(
            phone = "010-1234-1234",
            username = request.username,
            nickname = "name",
            password = PasswordEncryptUtil.encrypt(request.password),
            role = Role.USER
        )
        ReflectionTestUtils.setField(member, "sn", 1L)
        `when`("로그인 정보가 있을때") {
            every { memberRepository.findByUsername(any()) } returns member
            every { jwtTokenProvider.generateAccessToken(any()) } returns "token"
            val result = memberService.login(request)
            then("로그인이 완료된다.") {
                result.name shouldBe member.nickname
                result.phone shouldBe member.phone
                result.token shouldBe "token"
            }
        }

        `when`("로그인 정보가 없을때") {
            every { memberRepository.findByUsername(any()) } returns null
            val exception = shouldThrow<Exception> {
                memberService.login(request)
            }
            then("로그인이 실패한다.") {
                exception.message shouldBe "아이디가 존재하지 않거나 비밀번호가 틀렸습니다."
            }
        }

        request = MemberRequest.Login("username", "1q2w3e4r")
        `when`("비밀번호가 틀렸을때") {
            every { memberRepository.findByUsername(any()) } returns member
            val exception = shouldThrow<Exception> {
                memberService.login(request)
            }
            then("로그인이 실패한다.") {
                exception.message shouldBe "아이디가 존재하지 않거나 비밀번호가 틀렸습니다."
            }
        }
    }

    given("멤버 sn이 주어지면") {
        val memberSn = 1L
        val member = Member()
        every { memberRepository.getOrThrow(any()) } answers { member }
        When("내 정보를 조회하였을 때") {
            val result = memberService.findMemberInfo(memberSn)
            Then("정상적으로 조회된다.") {
                result shouldBe MemberDto.of(member)
            }
        }
    }

    given("멤버 sn과 nickname이 주어지면") {
        val memberSn = 1L
        val nickname = "nickname"
        val member = Member()
        every { memberRepository.getOrThrow(any()) } answers { member }
        When("닉네임을 변경하였을 때") {
            val result = memberService.updateNickname(memberSn, nickname)
            Then("정상적으로 변경된다.") {
                result.nickname shouldBe nickname
            }
        }
    }

    given("멤버 sn이 주어지면") {
        val memberSn = 1L
        val member = mockk<Member>()
        every { memberRepository.getOrThrow(any()) } answers { member }
        every { member.delete() } just Runs
        every { roomMemberRepository.deleteByMember(any()) } just Runs
        When("멤버를 삭제하였을 때") {
            memberService.deleteMember(memberSn)
            Then("정상적으로 삭제된다.") {
                verify(exactly = 1) { member.delete() }
                verify(exactly = 1) { roomMemberRepository.deleteByMember(any()) }
            }
        }
    }

    given("멤버 sn과 패스워드, 패스워드 확인이 주어지면") {
        val memberSn = 1L
        val password = "1q2w3e4r5t"
        val passwordCheck = "1q2w3e4r5t"
        val member = Member()
        every { memberRepository.getOrThrow(any()) } answers { member }
        When("패스워드를 변경하였을 때") {
            Then("정상적으로 변경된다.") {
                memberService.updatePassword(memberSn, password, passwordCheck)
            }
        }

        When("패스워드가 일치하지 않을 때") {
            every { memberRepository.getOrThrow(any()) } answers { member }
            val exception = shouldThrow<CustomException> {
                memberService.updatePassword(memberSn, password, "1q2w3e4r")
            }
            Then("예외가 발생한다.") {
                exception.message shouldBe ErrorMessage.INVALID_PASSWORD_CHECK.message
            }
        }
    }
})