package com.midasit.mcafe.domain.room.dto

import com.midasit.mcafe.model.RoomStatus
import io.swagger.v3.oas.annotations.media.Schema

class RoomRequest {
    @Schema(name = "RoomRequestCreate", description = "방 생성 정보")
    class Create(val name: String, val status: RoomStatus) {
        var password: String? = null
        constructor(name: String, status: RoomStatus, password: String)
                : this(name, status) {
            this.password = password
        }
    }

    @Schema(name = "RoomRequestEnterRoom", description = "방 입장 요청 객체")
    class EnterRoom() {
        var password: String? = null
        constructor(password: String) : this() {
            this.password = password
        }
    }
}