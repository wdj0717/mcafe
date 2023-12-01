package com.midasit.mcafe.domain.room.dto

import com.midasit.mcafe.domain.room.Room
import com.midasit.mcafe.model.RoomStatus
import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "RoomDto", description = "방 정보")
data class RoomDto(
    val sn: Long,
    val hostSn: Long,
    val hostName: String,
    val name: String,
    val status: RoomStatus
) {
    companion object {
        fun of(room: Room): RoomDto {
            return RoomDto(room.sn, room.host.sn, room.host.nickname, room.name, room.status)
        }
    }
}