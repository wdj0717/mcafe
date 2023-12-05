package com.midasit.mcafe.domain.room.dto

import com.midasit.mcafe.model.RoomStatus
import io.swagger.v3.oas.annotations.media.Schema

class RoomRequest {
    @Schema(name = "RoomRequestCreate", description = "방 생성 정보")
    class Create(val name: String, val status: RoomStatus, val password: String? = null)

    @Schema(name = "RoomRequestEnterRoom", description = "방 입장 요청 객체")
    class EnterRoom(val password: String? = null)

    @Schema(name = "RoomRequestUpdateRoom", description = "방 정보 수정 요청 객체")
    class UpdateRoom(val roomSn: Long, val name: String?, val status: RoomStatus?, val password: String? = null)
}