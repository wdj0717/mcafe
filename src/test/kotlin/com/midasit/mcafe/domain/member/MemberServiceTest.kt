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
import com.midasit.mcafe.model.getRandomBoolean
import com.midasit.mcafe.model.getRandomOf
import com.midasit.mcafe.model.getRandomPhone
import com.midasit.mcafe.model.getRandomSn
import com.midasit.mcafe.model.getRandomString
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
        roomMemberRepository = roomMemberRepository
    )

    afterContainer {
        clearAllMocks()
    }

    given("u chef 인증 정보가 주어지면") {
        val request = MemberRequest.UChefAuth(getRandomPhone(), getRandomString(10), getRandomString(10))
        val certKey = getRandomString(10)
        every { uChefComponent.login(any(), any(), any()) } answers { certKey }
        When("인증키를 요청하면") {
            val result = memberService.getUChefAuth(request)
            Then("인증키가 반환된다.") {
                result shouldBe certKey
            }
        }
    }

    given("username이 주어지면") {
        val username = getRandomString(10)
        val boolean = getRandomBoolean()
        every { memberRepository.existsByUsername(any()) } answers { boolean }
        When("username 중복검사를 요청하면") {
            val result = memberService.existsMemberByUsername(username)
            Then("중복검사 결과가 반환된다.") {
                result shouldBe boolean
            }
        }
    }

    given("회원가입을 위한 정보를 받아온다.") {
        val password = getRandomString(10)
        val request = MemberRequest.Signup(
            username = getRandomString(10),
            password = password,
            passwordCheck = password,
            nickname = getRandomString(10),
            certKey = getRandomString(10)
        )

        val member = Member(
            phone = getRandomPhone(),
            username = request.username,
            nickname = request.nickname,
            password = request.password,
            role = getRandomOf(Role.values())
        )
        every { memberRepository.save(any()) } answers { member }
        every { memberRepository.existsByUsername(any()) } answers { false }
        every { redisTemplate.opsForValue().getAndDelete(any()) } answers { request.certKey }
        `when`("회원가입을 요청한다.") {
            val result = memberService.signup(request)
            then("회원가입이 완료된다.") {
                result.nickname shouldBe member.nickname
                result.phone shouldBe member.phone
            }
        }

        every { memberRepository.existsByUsername(any()) } answers { true }
        `when`("중복 아이디가 있을때 회원가입을 요청하면.") {
            then("예외가 발생한다.") {
                shouldThrow<CustomException> {
                    memberService.signup(request)
                }
            }
        }

        every { memberRepository.existsByUsername(any()) } answers { false }
        every { redisTemplate.opsForValue().getAndDelete(any()) } answers { null }
        When("certKey가 존재하지 않으면") {
            then("예외가 발생한다.") {
                shouldThrow<CustomException> {
                    memberService.signup(request)
                }
            }
        }
    }

    given("로그인 정보를 받아온다.") {
        var request = MemberRequest.Login(username = getRandomString(10), getRandomString(10))
        val token = getRandomString(10)
        val member = Member(username = request.username, password = PasswordEncryptUtil.encrypt(request.password))
        `when`("로그인 정보가 있을때") {
            every { memberRepository.findByUsername(any()) } answers { member }
            every { jwtTokenProvider.generateAccessToken(any()) } answers { token }
            val result = memberService.login(request)
            then("로그인이 완료된다.") {
                result.name shouldBe member.nickname
                result.phone shouldBe member.phone
                result.token shouldBe token
            }
        }

        `when`("로그인 정보가 없을때") {
            every { memberRepository.findByUsername(any()) } answers { null }
            val exception = shouldThrow<Exception> {
                memberService.login(request)
            }
            then("로그인이 실패한다.") {
                exception.message shouldBe "아이디가 존재하지 않거나 비밀번호가 틀렸습니다."
            }
        }

        request = MemberRequest.Login(getRandomString(10), getRandomString(10))
        `when`("비밀번호가 틀렸을때") {
            every { memberRepository.findByUsername(any()) } answers { member }
            val exception = shouldThrow<Exception> {
                memberService.login(request)
            }
            then("로그인이 실패한다.") {
                exception.message shouldBe "아이디가 존재하지 않거나 비밀번호가 틀렸습니다."
            }
        }
    }

    given("멤버 sn이 주어지면") {
        val memberSn = getRandomSn()
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
        val memberSn = getRandomSn()
        val nickname = getRandomString(10)
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
        val memberSn = getRandomSn()
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
        val memberSn = getRandomSn()
        val password = getRandomString(10)
        val member = Member()
        every { memberRepository.getOrThrow(any()) } answers { member }
        When("패스워드를 변경하였을 때") {
            Then("정상적으로 변경된다.") {
                memberService.updatePassword(memberSn, password, password)
            }
        }

        When("패스워드가 일치하지 않을 때") {
            every { memberRepository.getOrThrow(any()) } answers { member }
            val exception = shouldThrow<CustomException> {
                memberService.updatePassword(memberSn, password, getRandomString(10))
            }
            Then("예외가 발생한다.") {
                exception.message shouldBe ErrorMessage.INVALID_PASSWORD_CHECK.message
            }
        }
    }
})