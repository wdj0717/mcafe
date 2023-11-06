package com.midasit.mcafe.domain.room

import org.springframework.data.jpa.repository.JpaRepository

interface RoomRepository: JpaRepository<Room, Long> {
    fun findByName(name: String): Room?
}