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
import com.midasit.mcafe.infra.exception.ErrorMessage
import com.midasit.mcafe.model.OrderStatus
import com.midasit.mcafe.model.RoomStatus
import com.midasit.mcafe.model.validate
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
        validate(ErrorMessage.DUPLICATE_ROOM_NAME) { duplicateRoomName(request.name) }
        val member = memberService.findBySn(memberSn)
        val createdRoom = roomRepository.save(Room(request.name, request.password, member, request.status))
        roomMemberRepository.save(RoomMember(member, createdRoom))

        return RoomDto.of(createdRoom)
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
            validate(ErrorMessage.INVALID_ROOM_PASSWORD) { room.password == password }
        }
        validate(ErrorMessage.INVALID_ROOM_INFO) { room.status != RoomStatus.CLOSED }

        val notEntered = !roomMemberRepository.existsByRoomAndMember(room, member)
        validate(ErrorMessage.ALREADY_ENTERED_ROOM) { notEntered }

        roomMemberRepository.save(RoomMember(member, room))

        return true
    }

    @Transactional
    fun updateRoom(memberSn: Long, roomSn: Long, name: String?, status: RoomStatus?, password: String?): Boolean {
        val member = memberService.findBySn(memberSn)
        val room = this.findBySn(roomSn)
        validate(ErrorMessage.INVALID_ROOM_INFO) { room.status != RoomStatus.CLOSED }
        validate(ErrorMessage.INVALID_ROOM_INFO) { room.host.sn == member.sn }
        validate(ErrorMessage.INVALID_ROOM_INFO) { status != RoomStatus.PRIVATE || password != null }

        room.updateRoom(name, status, password)

        return true
    }

    @Transactional
    fun exitRoom(memberSn: Long, roomSn: Long): Boolean {
        val member = memberService.findBySn(memberSn)
        val room = this.findBySn(roomSn)
        this.checkMemberInRoom(member, room)
        validate(ErrorMessage.HOST_CANT_EXIT) { room.host != member }
        roomMemberRepository.deleteByRoomAndMember(room, member)

        return true
    }

    @Transactional
    fun deleteRoom(memberSn: Long, roomSn: Long): Boolean {
        val member = memberService.findBySn(memberSn)
        val room = this.findBySn(roomSn)
        validate(ErrorMessage.INVALID_ROOM_INFO) { room.host.sn == member.sn }
        room.updateRoomStatus(RoomStatus.CLOSED)

        return true
    }

    fun checkMemberInRoom(member: Member, room: Room) {
        validate(ErrorMessage.INVALID_ROOM_INFO) { room.status != RoomStatus.CLOSED }
        validate(ErrorMessage.INVALID_ROOM_INFO) {
            roomMemberRepository.existsByRoomAndMember(
                room,
                member
            )
        }
    }

    fun findBySn(roomSn: Long): Room {
        return roomRepository.getOrThrow(roomSn)
    }

    private fun duplicateRoomName(name: String): Boolean {
        return !roomRepository.existsByNameAndStatusNot(name, RoomStatus.CLOSED)
    }
}