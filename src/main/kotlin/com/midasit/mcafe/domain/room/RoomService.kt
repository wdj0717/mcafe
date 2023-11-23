package com.midasit.mcafe.domain.room

import com.midasit.mcafe.domain.member.MemberService
import com.midasit.mcafe.domain.room.dto.RoomDto
import com.midasit.mcafe.domain.room.dto.RoomRequest
import com.midasit.mcafe.model.RoomStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class RoomService(
    val roomRepository: RoomRepository,
    val memberService: MemberService
) {

    @Transactional
    fun createRoom(request: RoomRequest.Create, memberSn: Long): RoomDto {
        require(this.duplicateRoomName(request.name)) { "이미 존재하는 방 이름입니다." }
        val member = memberService.findBySn(memberSn)
        val createRoom = Room(request.name, request.password, member, request.status)
        return RoomDto.of(roomRepository.save(createRoom))
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
        return roomRepository.findByName(name) == null
    }
}