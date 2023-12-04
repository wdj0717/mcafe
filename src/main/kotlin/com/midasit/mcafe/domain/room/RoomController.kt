package com.midasit.mcafe.domain.room

import com.midasit.mcafe.domain.room.dto.RoomRequest
import com.midasit.mcafe.domain.room.dto.RoomResponse
import com.midasit.mcafe.infra.exception.CustomException
import com.midasit.mcafe.infra.exception.ErrorMessage
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@Tag(name = "방 컨트롤러")
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

    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "방 입장 성공"),
            ApiResponse(
                responseCode = "409",
                description = "이미 입장한 방입니다.",
                content = [Content(schema = Schema(implementation = ErrorMessage::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 방 정보입니다.",
                content = [Content(schema = Schema(implementation = ErrorMessage::class))]
            ),
            ApiResponse(
                responseCode = "401",
                description = "방 비밀번호가 틀렸습니다.",
                content = [Content(schema = Schema(implementation = ErrorMessage::class))]
            )
        ]
    )
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
    ): RoomResponse.GetRoomInfo {
        val memberSn = getMemberSn(authentication)
        return roomService.getRoomInfo(memberSn, roomSn)
    }

    @Operation(summary = "입장한 방 목록 조회")
    @GetMapping("/entered")
    fun getEnteredRoomList(authentication: Authentication): RoomResponse.GetRoomList {
        val memberSn = getMemberSn(authentication)
        return RoomResponse.GetRoomList(roomService.getEnteredRoomList(memberSn))
    }

    @Operation(summary = "방 정보 수정")
    @PatchMapping
    fun updateRoom(@RequestBody rq: RoomRequest.UpdateRoom, authentication: Authentication): Boolean {
        val memberSn = getMemberSn(authentication)
        return roomService.updateRoom(memberSn, rq.roomSn, rq.name, rq.status, rq.password)
    }

    @Operation(summary = "방 나가기")
    @DeleteMapping("/exit/{roomSn}")
    fun exitRoom(
        @PathVariable roomSn: Long,
        authentication: Authentication
    ): Boolean {
        val memberSn = getMemberSn(authentication)
        return roomService.exitRoom(memberSn, roomSn)
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