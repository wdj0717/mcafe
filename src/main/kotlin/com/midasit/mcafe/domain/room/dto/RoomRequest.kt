package com.midasit.mcafe.domain.room.dto

import com.midasit.mcafe.model.RoomStatus
import io.swagger.v3.oas.annotations.media.Schema

class RoomRequest {
    @Schema(name = "RoomRequestCreate", description = "방 생성 정보")
    class Create(val name: String, val password: String, val status: RoomStatus)
}