package com.midasit.mcafe.domain.room

import com.midasit.mcafe.domain.member.MemberService
import com.midasit.mcafe.domain.room.dto.RoomDto
import com.midasit.mcafe.domain.room.dto.RoomRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class RoomService(val roomRepository: RoomRepository,
                  val memberService: MemberService) {

    @Transactional
    fun createRoom(request: RoomRequest.Create, memberSn: Long): RoomDto {
        require(this.duplicateRoomName(request.name)) { "이미 존재하는 방 이름입니다." }
        val member = memberService.findBySn(memberSn)
        val createRoom = Room(request.name, request.password, member, request.status)
        return RoomDto.of(roomRepository.save(createRoom))
    }

    private fun duplicateRoomName(name: String): Boolean {
        return roomRepository.findByName(name) == null
    }
}