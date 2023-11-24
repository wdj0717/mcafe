package com.midasit.mcafe.domain.member

import com.midasit.mcafe.domain.member.dto.MemberRequest
import com.midasit.mcafe.infra.component.UChefComponent
import com.midasit.mcafe.infra.config.jwt.JwtTokenProvider
import com.midasit.mcafe.model.PasswordEncryptUtil
import com.midasit.mcafe.model.Role
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.test.util.ReflectionTestUtils

class MemberServiceTest : BehaviorSpec({
    val memberRepository = mockk<MemberRepository>(relaxed = true)
    val jwtTokenProvider = mockk<JwtTokenProvider>(relaxed = true)
    val uChefComponent = mockk<UChefComponent>(relaxed = true)
    val redisTemplate = mockk<RedisTemplate<String, Any>>(relaxed = true)
    val memberService = MemberService(uChefComponent, memberRepository, jwtTokenProvider, redisTemplate)

    given("회원가입을 위한 정보를 받아온다.") {
        val request = MemberRequest.Signup("username", "1q2w3e4r5t", "1q2w3e4r5t", "name", "certKey")
        val member = Member(
            phone = "010-1234-5678",
            username = request.username,
            nickName = request.nickname,
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
            nickName = "name",
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
})