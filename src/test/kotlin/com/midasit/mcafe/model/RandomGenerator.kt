package com.midasit.mcafe.model

import com.midasit.mcafe.domain.member.Member
import org.springframework.test.util.ReflectionTestUtils

fun Member(memberSn: Long = 0L): Member {
    val member = Member(
        phone = "010-1234-5678",
        username = "test",
        password = "test",
        nickname = "test",
        role = Role.USER
    )
    ReflectionTestUtils.setField(member, "sn", memberSn)
    return member
}