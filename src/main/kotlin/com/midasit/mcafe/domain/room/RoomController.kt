package com.midasit.mcafe.domain.room

import com.midasit.mcafe.domain.room.dto.RoomRequest
import com.midasit.mcafe.domain.room.dto.RoomResponse
import com.midasit.mcafe.infra.exception.CustomException
import com.midasit.mcafe.infra.exception.ErrorMessage
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

    @Operation(summary = "방 입장")
    @PostMapping("/enter/{roomSn}")
    fun enterRoom(
        @PathVariable roomSn: Long,
        @RequestBody request: RoomRequest.EnterRoom,
        authentication: Authentication
    ): Boolean {
        val memberSn = getMemberSn(authentication)
        return roomService.enterRoom(memberSn, roomSn, request.password)
    }

    @Operation(summary = "방 목록 조회")
    @GetMapping
    fun getRoomList(authentication: Authentication): RoomResponse.GetRoomList {
        return RoomResponse.GetRoomList(roomService.getRoomList())
    }

    @Operation(summary = "방 정보 조회")
    @GetMapping("/{roomSn}")
    fun getRoomInfo(
        @PathVariable roomSn: Long,
        authentication: Authentication
    ): RoomResponse.GetRoomList {
        return RoomResponse.GetRoomList(roomService.getRoomList())
    }

    @Operation(summary = "입장한 방 목록 조회")
    @GetMapping("/entered")
    fun getEnteredRoomList(authentication: Authentication): RoomResponse.GetRoomList {
        val memberSn = getMemberSn(authentication)
        return RoomResponse.GetRoomList(roomService.getEnteredRoomList(memberSn))
    }

    @Operation(summary = "방 나가기")
    @DeleteMapping("/exit/{roomSn}")
    fun exitRoom(
        @PathVariable roomSn: Long,
        authentication: Authentication
    ): Boolean {
        val memberSn = getMemberSn(authentication)
        require(roomService.exitRoom(memberSn, roomSn) > 0) { throw CustomException(ErrorMessage.INVALID_ROOM_INFO) }

        return true
    }

    @Operation(summary = "방 삭제")
    @DeleteMapping("/{roomSn}")
    fun deleteRoom(
        @PathVariable roomSn: Long,
        authentication: Authentication
    ): Boolean {
        val memberSn = getMemberSn(authentication)
        require(roomService.deleteRoom(memberSn, roomSn)) { throw CustomException(ErrorMessage.INVALID_ROOM_INFO) }

        return true
    }

    private fun getMemberSn(authentication: Authentication): Long {
        return (authentication.principal as Long)
    }
}