package com.midasit.mcafe.domain.room

import com.midasit.mcafe.model.RoomStatus
import org.springframework.data.jpa.repository.JpaRepository

interface RoomRepository : JpaRepository<Room, Long> {
    fun findByName(name: String): Room?
    fun findAllByStatusNot(roomStatus: RoomStatus): List<Room>
}