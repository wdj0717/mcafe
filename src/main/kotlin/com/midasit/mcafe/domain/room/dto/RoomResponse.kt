package com.midasit.mcafe.domain.room.dto

import com.midasit.mcafe.model.RoomStatus
import io.swagger.v3.oas.annotations.media.Schema

class RoomResponse {

    @Schema(name = "RoomResponseCreate", description = "방 생성 결과")
    class Create(val sn: Long, val name: String, val status: RoomStatus) {
        companion object {
            fun of(roomDto: RoomDto): Create {
                return Create(roomDto.sn, roomDto.name, roomDto.status)
            }
        }
    }

    @Schema(name = "RoomResponseGetRoomList", description = "방 목록 조회")
    class GetRoomList(@Schema(description = "방 목록", required = true)
                      val roomList: List<RoomDto>) {
    }
}