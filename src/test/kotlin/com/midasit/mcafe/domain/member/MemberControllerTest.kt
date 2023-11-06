package com.midasit.mcafe.domain.member

import com.midasit.mcafe.domain.member.dto.LoginDto
import com.midasit.mcafe.domain.member.dto.MemberDto
import com.midasit.mcafe.domain.member.dto.MemberRequest
import com.midasit.mcafe.domain.member.dto.MemberResponse
import com.midasit.mcafe.model.ControllerTest
import com.midasit.mcafe.model.Role
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.mockito.InjectMocks
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class MemberControllerTest : ControllerTest() {

    private val memberService: MemberService = mockk()

    @InjectMocks
    private val memberController =  MemberController(memberService)

    override fun getController(): Any {
        return memberController
    }

    init {
        given("회원가입을 위한 정보를 받아온다.") {
            val request = MemberRequest.Signup(phone = "010-1234-5678", name = "name", password = "1q2w3e4r5t")
            every { memberService.signup(any()) } returns MemberDto("name", "010-1234-5678", Role.USER)
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
                    result.name shouldBe "name"
                    result.phone shouldBe "010-1234-5678"
                }
            }
        }

        given("로그인 정보를 받아온다") {
            val request = MemberRequest.Login("010-1234-5678", "1q2w3e4r5t")
            val loginDto = LoginDto(phone = "name", name = "010-1234-5678")
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
    }


}