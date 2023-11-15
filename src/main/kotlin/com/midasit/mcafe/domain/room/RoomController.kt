package com.midasit.mcafe.domain.room

import com.midasit.mcafe.domain.room.dto.RoomRequest
import com.midasit.mcafe.domain.room.dto.RoomResponse
import io.swagger.v3.oas.annotations.Operation
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/room")
class RoomController(val roomService: RoomService) {

    @Operation(summary = "방 생성")
    @PostMapping
    fun createRoom(
        @RequestBody request: RoomRequest.Create,
        authentication: Authentication
    ): RoomResponse.Create {
        val memberSn = getMemberSn(authentication)
        return RoomResponse.Create.of(roomService.createRoom(request, memberSn))
    }

    @Operation(summary = "방 목록 조회")
    @GetMapping
    fun getRoomList(authentication: Authentication): RoomResponse.GetRoomList {
        return RoomResponse.GetRoomList(roomService.getRoomList())
    }

    private fun getMemberSn(authentication: Authentication): Long {
        return (authentication.principal as Long)
    }
}