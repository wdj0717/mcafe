package com.midasit.mcafe.domain.room.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.midasit.mcafe.domain.room.Room
import com.midasit.mcafe.model.RoomStatus
import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "RoomDto", description = "방 정보")
@JsonInclude(JsonInclude.Include.NON_NULL)
data class RoomDto(val sn: Long,val hostName: String, val name: String, val status: RoomStatus) {
    companion object {
        fun of(room: Room): RoomDto {
            room.sn ?: throw IllegalArgumentException("Room id must not be null")
            return RoomDto(room.sn, room.host.name, room.name, room.status)
        }
    }
}