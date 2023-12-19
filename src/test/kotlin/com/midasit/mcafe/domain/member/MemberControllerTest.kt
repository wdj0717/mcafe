package com.midasit.mcafe.domain.member

import com.midasit.mcafe.domain.member.dto.LoginDto
import com.midasit.mcafe.domain.member.dto.MemberDto
import com.midasit.mcafe.domain.member.dto.MemberRequest
import com.midasit.mcafe.domain.member.dto.MemberResponse
import com.midasit.mcafe.model.ControllerTest
import com.midasit.mcafe.model.Member
import com.midasit.mcafe.model.getRandomPhone
import com.midasit.mcafe.model.getRandomSn
import com.midasit.mcafe.model.getRandomString
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.mockito.InjectMocks
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class MemberControllerTest : ControllerTest() {

    private val memberService: MemberService = mockk()

    @InjectMocks
    private val memberController = MemberController(memberService)

    override fun getController(): Any {
        return memberController
    }

    init {

        afterContainer {
            clearAllMocks()
        }

        given("u chef 정보가 주어지면") {
            val request = MemberRequest.UChefAuth(getRandomPhone(), getRandomString(10), getRandomString(10))
            val certKey = getRandomString(10)
            every { memberService.getUChefAuth(any()) } answers { certKey }
            When("API를 호출하면") {
                val res = perform(
                    post("/member/uchef-auth")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON_VALUE)
                ).andExpect { status().isOk }.andReturn()
                Then("인증키가 반환된다.") {
                    val response = res.response.contentAsString
                    val result = getObject(response, MemberResponse.CertKey::class.java)
                    result.certKey shouldBe certKey
                }
            }
        }

        given("username 정보가 주어지면") {
            val username = getRandomString(10)
            every { memberService.existsMemberByUsername(any()) } answers { true }
            When("API를 호출하면") {
                val res = perform(get("/member/id-check/$username")).andExpect { status().isOk }.andReturn()
                Then("중복여부가 반환된다.") {
                    val response = res.response.contentAsString
                    val result = getObject(response, MemberResponse.UsernameCheck::class.java)
                    result.isPossible shouldBe false
                }
            }
        }

        given("회원가입을 위한 정보를 받아온다.") {
            val request = MemberRequest.Signup(
                username = getRandomString(10),
                password = getRandomString(10),
                passwordCheck = getRandomString(10),
                nickname = getRandomString(10),
                certKey = getRandomString(10)
            )
            val member = Member()
            every { memberService.signup(any()) } answers { MemberDto.of(member) }
            `when`("회원가입을 요청한다.") {
                val res = perform(
                    post("/member/signup")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON_VALUE)
                ).andExpect {
                    status().isOk
                }.andReturn()
                then("회원가입이 완료된다.") {
                    val response = res.response.contentAsString
                    val result = getObject(response, MemberResponse.Result::class.java)
                    result.username shouldBe member.username
                    result.phone shouldBe member.phone
                }
            }
        }

        given("로그인 정보를 받아온다") {
            val request = MemberRequest.Login(getRandomPhone(), getRandomString(10))
            val loginDto = LoginDto(sn = getRandomSn(), phone = getRandomPhone(), name = getRandomString(10))
            every { memberService.login(any()) } returns loginDto
            `when`("로그인을 요청한다.") {
                val res = perform(
                    post("/member/login")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON_VALUE)
                ).andExpect {
                    status().isOk
                }.andReturn()
                then("로그인이 완료된다.") {
                    val response = res.response.contentAsString
                    val result = getObject(response, MemberResponse.Login::class.java)
                    result.name shouldBe loginDto.name
                    result.phone shouldBe loginDto.phone
                    result.token shouldBe loginDto.token
                }
            }
        }

        given("내정보를 확인하려고 할때") {
            val member = Member()
            every { memberService.findMemberInfo(any()) } answers { MemberDto.of(member) }
            When("API를 호출하면") {
                val res = perform(get("/member"))
                    .andExpect { status().isOk }
                    .andReturn()
                Then("내정보가 반환된다.") {
                    val response = res.response.contentAsString
                    val result = getObject(response, MemberResponse.Result::class.java)
                    result.nickname shouldBe member.nickname
                    result.username shouldBe member.username
                    result.phone shouldBe member.phone
                }
            }
        }

        given("닉네임 정보가 주어지면") {
            val request = MemberRequest.Nickname(getRandomString(10))
            val member = Member()
            every { memberService.updateNickname(any(), any()) } answers { MemberDto.of(member) }
            When("닉네임 API를 호출하면") {
                val res = perform(
                    put("/member/nickname")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON_VALUE)
                ).andExpect { status().isOk }.andReturn()
                Then("닉네임이 변경된다.") {
                    val response = res.response.contentAsString
                    val result = getObject(response, MemberResponse.Result::class.java)
                    result.nickname shouldBe member.nickname
                }
            }
        }

        given("비밀번호 정보가 주어지면") {
            val request = MemberRequest.Password(getRandomString(10), getRandomString(10))
            val member = Member()
            every { memberService.updatePassword(any(), any(), any()) } answers { MemberDto.of(member) }
            When("비밀번호 API를 호출하면") {
                val res = perform(
                    put("/member/password")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON_VALUE)
                ).andExpect { status().isOk }.andReturn()
                Then("비밀번호가 변경된다.") {
                    val response = res.response.contentAsString
                    val result = getObject(response, MemberResponse.Result::class.java)
                    result.nickname shouldBe member.nickname
                }
            }
        }

        given("회원탈퇴를 요청하면") {
            every { memberService.deleteMember(any()) } just Runs
            When("API를 호출하면") {
                perform(delete("/member")).andExpect { status().isOk }
                then("회원탈퇴가 완료된다.") {
                    verify(exactly = 1) { memberService.deleteMember(any()) }
                }
            }
        }
    }
}