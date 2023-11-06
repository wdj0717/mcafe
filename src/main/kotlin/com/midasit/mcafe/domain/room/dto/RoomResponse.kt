package com.midasit.mcafe.domain.room.dto

import com.midasit.mcafe.model.RoomStatus
import io.swagger.v3.oas.annotations.media.Schema

class RoomResponse {

    @Schema(name = "RoomResponseResult", description = "방 생성 결과")
    class Result(val id: Long, val name: String, val password: String, val status: RoomStatus) {
        companion object {
            fun of(roomDto: RoomDto): Result {
                return Result(roomDto.id, roomDto.name, roomDto.password, roomDto.status)
            }
        }
    }
}