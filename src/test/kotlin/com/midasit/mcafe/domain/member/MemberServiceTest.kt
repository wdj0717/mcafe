package com.midasit.mcafe.domain.member

import com.midasit.mcafe.domain.member.dto.MemberRequest
import com.midasit.mcafe.infra.config.jwt.JwtTokenProvider
import com.midasit.mcafe.model.PasswordEncryptUtil
import com.midasit.mcafe.model.Role
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class MemberServiceTest : BehaviorSpec({
    val memberRepository = mockk<MemberRepository>(relaxed = true)
    val jwtTokenProvider = mockk<JwtTokenProvider>(relaxed = true)
    val memberService = MemberService(memberRepository, jwtTokenProvider)
    given("회원가입을 위한 정보를 받아온다.") {
        val request = MemberRequest.Signup(phone = "010-1234-5678", name = "name", password = "1q2w3e4r5t")
        val member = Member(
            phone = request.phone,
            name = request.name,
            password = request.password,
            role = Role.USER
        )
        every { memberRepository.save(any()) } returns member
        `when`("회원가입을 요청한다.") {
            val result = memberService.signup(request)
            then("회원가입이 완료된다.") {
                result.name shouldBe member.name
                result.phone shouldBe member.phone
            }
        }
    }

    given("로그인 정보를 받아온다.") {
        var request = MemberRequest.Login("010-1234-5678", "1q2w3e4r5t")
        val member = Member(
            phone = request.phone,
            name = "name",
            password = PasswordEncryptUtil.encrypt(request.password),
            role = Role.USER
        )
        `when`("로그인 정보가 있을때") {
            every { memberRepository.findByPhone(any()) } returns member
            every { jwtTokenProvider.generateAccessToken(any()) } returns "token"
            val result = memberService.login(request)
            then("로그인이 완료된다.") {
                result.name shouldBe member.name
                result.phone shouldBe member.phone
                result.token shouldBe "token"
            }
        }

        `when`("로그인 정보가 없을때") {
            every { memberRepository.findByPhone(any()) } returns null
            val exception = shouldThrow<Exception> {
                memberService.login(request)
            }
            then("로그인이 실패한다.") {
                exception.message shouldBe "로그인 정보가 없습니다."
            }
        }

        request = MemberRequest.Login("010-1234-5678", "1q2w3e4r")
        `when`("비밀번호가 틀렸을때") {
            every { memberRepository.findByPhone(any()) } returns member
            val exception = shouldThrow<Exception> {
                memberService.login(request)
            }
            then("로그인이 실패한다.") {
                exception.message shouldBe "비밀번호가 일치하지 않습니다."
            }
        }
    }
})