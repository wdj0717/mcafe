package com.midasit.mcafe.domain.roommember.dto

import com.midasit.mcafe.domain.member.Member
import com.midasit.mcafe.domain.order.dto.OrderDto
import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "MemberDto", description = "회원 정보")
class RoomMemberDto(var memberSn: Long, var nickname: String, var order: OrderDto) {
    companion object {
        fun of(member: Member, order: OrderDto): RoomMemberDto {
            return RoomMemberDto(member.sn, member.nickname, order)
        }
    }
}