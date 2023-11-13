package com.midasit.mcafe.domain.room

import com.midasit.mcafe.domain.member.MemberService
import com.midasit.mcafe.domain.room.dto.RoomDto
import com.midasit.mcafe.domain.room.dto.RoomRequest
import com.midasit.mcafe.domain.roommember.RoomMember
import com.midasit.mcafe.domain.roommember.RoomMemberRepository
import com.midasit.mcafe.infra.exception.CustomException
import com.midasit.mcafe.infra.exception.ErrorMessage
import com.midasit.mcafe.model.RoomStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class RoomService(
    val roomRepository: RoomRepository,
    val roomMemberRepository: RoomMemberRepository,
    val memberService: MemberService,
) {
    @Transactional
    fun createRoom(request: RoomRequest.Create, memberSn: Long): RoomDto {
        require(this.duplicateRoomName(request.name)) { throw CustomException(ErrorMessage.DUPLICATE_ROOM_NAME) }
        val member = memberService.findBySn(memberSn)
        val createdRoom = roomRepository.save(Room(request.name, request.password, member, request.status))
        roomMemberRepository.save(RoomMember(member, createdRoom))

        return RoomDto.of(createdRoom)
    }

    fun getRoomList(): List<RoomDto> {
        val roomList = roomRepository.findAllByStatusNot(RoomStatus.CLOSED)
        return roomList.map { RoomDto.of(it) }
    }

    fun findRoomSn(roomSn: Long): Room {
        return roomRepository.findById(roomSn)
            .orElseThrow { IllegalArgumentException("존재하지 않는 방입니다.") }
    }

    private fun duplicateRoomName(name: String): Boolean {
        return !roomRepository.existsByName(name)
    }

    fun getEnteredRoomList(memberSn: Long): List<RoomDto> {
        val member = memberService.findBySn(memberSn)

        // TODO: @Richard queryDsl 도입하여 깔끔하게 할 수 있지 않을까?
        val roomMember = roomMemberRepository.findByMember(member)
        return roomMember.map { RoomDto.of(it.room) }
            .filter { it.status != RoomStatus.CLOSED }
    }

    @Transactional
    fun enterRoom(memberSn: Long, roomSn: Long, password: String?): Boolean {
        val member = memberService.findBySn(memberSn)
        val room = this.findBySn(roomSn)

        if (room.status == RoomStatus.PRIVATE && room.password != password) {
            require(room.password == password) { throw CustomException(ErrorMessage.INVALID_ROOM_PASSWORD) }
        }
        require(room.status != RoomStatus.CLOSED) { throw CustomException(ErrorMessage.INVALID_ROOM_INFO) }

        val notEntered = !roomMemberRepository.existsByRoomAndMember(room, member)
        require(notEntered) { throw CustomException(ErrorMessage.ALREADY_ENTERED_ROOM) }

        roomMemberRepository.save(RoomMember(member, room))

        return true
    }

    fun findBySn(roomSn: Long): Room {
        return roomRepository.findBySn(roomSn) ?: throw CustomException(ErrorMessage.INVALID_ROOM_INFO)
    }
}