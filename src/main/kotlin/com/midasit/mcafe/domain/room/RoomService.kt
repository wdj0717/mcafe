package com.midasit.mcafe.domain.room

import com.midasit.mcafe.domain.member.Member
import com.midasit.mcafe.domain.member.MemberService
import com.midasit.mcafe.domain.order.OrderRepository
import com.midasit.mcafe.domain.order.dto.OrderDto
import com.midasit.mcafe.domain.room.dto.RoomDto
import com.midasit.mcafe.domain.room.dto.RoomRequest
import com.midasit.mcafe.domain.room.dto.RoomResponse
import com.midasit.mcafe.domain.roommember.RoomMember
import com.midasit.mcafe.domain.roommember.RoomMemberRepository
import com.midasit.mcafe.domain.roommember.dto.RoomMemberDto
import com.midasit.mcafe.infra.component.UChefComponent
import com.midasit.mcafe.infra.exception.CustomException
import com.midasit.mcafe.infra.exception.ErrorMessage
import com.midasit.mcafe.model.OrderStatus
import com.midasit.mcafe.model.RoomStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class RoomService(
    val roomRepository: RoomRepository,
    val roomMemberRepository: RoomMemberRepository,
    val orderRepository: OrderRepository,
    val memberService: MemberService,
    val uChefComponent: UChefComponent
) {
    @Transactional
    fun createRoom(request: RoomRequest.Create, memberSn: Long): RoomDto {
        require(this.duplicateRoomName(request.name)) { throw CustomException(ErrorMessage.DUPLICATE_ROOM_NAME) }
        val member = memberService.findBySn(memberSn)
        val createdRoom = roomRepository.save(Room(request.name, request.password, member, request.status))
        roomMemberRepository.save(RoomMember(member, createdRoom))

        return RoomDto.of(createdRoom)
    }

    private fun duplicateRoomName(name: String): Boolean {
        return !roomRepository.existsByName(name)
    }

    fun getRoomList(): List<RoomDto> {
        val roomList = roomRepository.findAllByStatusNot(RoomStatus.CLOSED)
        return roomList.map { RoomDto.of(it) }
    }

    fun getRoomInfo(memberSn: Long, roomSn: Long): RoomResponse.GetRoomInfo {
        val member = memberService.findBySn(memberSn)
        val room = this.findBySn(roomSn)
        checkMemberInRoom(member, room)

        val roomMember = roomMemberRepository.findByRoom(room)
        val orderList = orderRepository.findByRoomAndStatus(room, OrderStatus.PENDING)
        val orderDtoList = orderList.map { OrderDto.of(it, uChefComponent.getMenuInfo(it.menuCode)) }
        val memberList = roomMember.map { RoomMemberDto.of(it.member) }

        return RoomResponse.GetRoomInfo(RoomDto.of(room), memberList, orderDtoList)
    }

    fun findByRoomSn(roomSn: Long): Room {
        return roomRepository.findById(roomSn)
            .orElseThrow { IllegalArgumentException("존재하지 않는 방입니다.") }
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

        if (room.status == RoomStatus.PRIVATE) {
            require(room.password == password) { throw CustomException(ErrorMessage.INVALID_ROOM_PASSWORD) }
        }
        require(room.status != RoomStatus.CLOSED) { throw CustomException(ErrorMessage.INVALID_ROOM_INFO) }

        val notEntered = !roomMemberRepository.existsByRoomAndMember(room, member)
        require(notEntered) { throw CustomException(ErrorMessage.ALREADY_ENTERED_ROOM) }

        roomMemberRepository.save(RoomMember(member, room))

        return true
    }

    @Transactional
    fun exitRoom(memberSn: Long, roomSn: Long): Boolean {
        val member = memberService.findBySn(memberSn)
        val room = this.findBySn(roomSn)
        this.checkMemberInRoom(member, room)
        require(room.host != member) { throw CustomException(ErrorMessage.HOST_CANT_EXIT) }
        room.updateRoomStatus(RoomStatus.CLOSED)

        return true
    }

    @Transactional
    fun deleteRoom(memberSn: Long, roomSn: Long): Boolean {
        val member = memberService.findBySn(memberSn)
        val room = this.findBySn(roomSn)
        require(room.host.sn == member.sn) { throw CustomException(ErrorMessage.INVALID_ROOM_INFO) }

        roomMemberRepository.deleteByRoom(room)
        roomRepository.delete(room)

        return true
    }

    fun checkMemberInRoom(member: Member, room: Room) {
        require(room.status != RoomStatus.CLOSED) { throw CustomException(ErrorMessage.INVALID_ROOM_INFO) }
        require(
            roomMemberRepository.existsByRoomAndMember(
                room,
                member
            )
        ) { throw CustomException(ErrorMessage.INVALID_ROOM_INFO) }

    }

    fun findBySn(roomSn: Long): Room {
        return roomRepository.findBySn(roomSn) ?: throw CustomException(ErrorMessage.INVALID_ROOM_INFO)
    }
}