package com.midasit.mcafe.domain.room.dto

import com.midasit.mcafe.domain.room.Room
import com.midasit.mcafe.model.RoomStatus
import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "RoomDto", description = "방 정보")
data class RoomDto(val id: Long, val name: String, val password: String, val status: RoomStatus) {
    companion object {
        fun of(room: Room): RoomDto {
            room.id ?: throw IllegalArgumentException("Room id must not be null")
            return RoomDto(room.id, room.name, room.password, room.status)
        }
    }
}