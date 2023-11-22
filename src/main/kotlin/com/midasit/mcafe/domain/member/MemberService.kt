package com.midasit.mcafe.domain.member

import com.midasit.mcafe.domain.member.dto.LoginDto
import com.midasit.mcafe.domain.member.dto.MemberDto
import com.midasit.mcafe.domain.member.dto.MemberRequest
import com.midasit.mcafe.infra.component.UChefComponent
import com.midasit.mcafe.infra.config.jwt.JwtTokenProvider
import com.midasit.mcafe.infra.exception.CustomException
import com.midasit.mcafe.infra.exception.ErrorMessage
import com.midasit.mcafe.model.PasswordEncryptUtil
import com.midasit.mcafe.model.Role
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class MemberService(
    private val uChefComponent: UChefComponent,
    private val memberRepository: MemberRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val redisTemplate: RedisTemplate<String, Any>
) {

    fun getUChefAuth(request: MemberRequest.UChefAuth): String {
        return uChefComponent.login(request.phone, request.securityId, request.password)
    }

    fun existsMemberByUsername(username: String): Boolean {
        return memberRepository.existsByUsername(username)
    }

    @Transactional
    fun signup(request: MemberRequest.Signup): MemberDto {
        val phone = validateMember(request)

        val member = Member(
            phone = phone,
            username = request.username,
            password = request.password,
            nickname = request.nickname,
            role = Role.USER
        )
        return MemberDto.of(memberRepository.save(member))
    }

    fun login(request: MemberRequest.Login): LoginDto {
        return memberRepository.findByUsername(request.username)?.let { member ->
            if (PasswordEncryptUtil.match(request.password, member.password).not()) {
                throw CustomException(ErrorMessage.INVALID_LOGIN_REQUEST)
            }
            require(member.sn != null) { throw CustomException(ErrorMessage.INVALID_LOGIN_REQUEST) }
            val accessToken = jwtTokenProvider.generateAccessToken(member.sn)
            LoginDto(phone = member.phone, name = member.nickname, token = accessToken)
        } ?: throw CustomException(ErrorMessage.INVALID_LOGIN_REQUEST)
    }

    fun findBySn(memberSn: Long): Member {
        return memberRepository.findBySn(memberSn) ?: throw CustomException(ErrorMessage.INVALID_LOGIN_REQUEST)
    }

    private fun validateMember(request: MemberRequest.Signup): String {
        // 비밀번호 체크 검사
        require(request.password == request.passwordCheck) { throw CustomException(ErrorMessage.INVALID_PASSWORD_CHECK) }

        // 아이디 중복체크 검사
        require(!memberRepository.existsByUsername(request.username)) { throw CustomException(ErrorMessage.DUPLICATE_ID) }

        // u chef 인증 검사
        val valueOperations = redisTemplate.opsForValue()
        val phone =
            valueOperations.getAndDelete(request.certKey) ?: throw CustomException(ErrorMessage.INVALID_UCHEF_AUTH)

        return phone.toString()
    }
}