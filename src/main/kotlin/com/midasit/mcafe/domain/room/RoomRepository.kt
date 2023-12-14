package com.midasit.mcafe.domain.room

import com.midasit.mcafe.infra.exception.CustomException
import com.midasit.mcafe.infra.exception.ErrorMessage
import com.midasit.mcafe.model.RoomStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.findByIdOrNull

fun RoomRepository.getOrThrow(sn: Long): Room =
    findByIdOrNull(sn) ?: throw CustomException(ErrorMessage.INVALID_ROOM_INFO)

interface RoomRepository : JpaRepository<Room, Long> {
    fun existsByNameAndStatusNot(name: String, roomStatus: RoomStatus): Boolean
    fun findAllByStatusNot(roomStatus: RoomStatus): List<Room>
}