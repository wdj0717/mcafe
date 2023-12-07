package com.midasit.mcafe.model

import com.midasit.mcafe.domain.member.Member

fun Member(): Member {
    return Member(
        "010-1234-1234",
        "test",
        "test",
        "test",
        Role.USER,
    )
}