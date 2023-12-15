package com.midasit.mcafe.domain.gamedata

import com.midasit.mcafe.model.GameType
import com.midasit.mcafe.model.ReadyStatus
import jakarta.validation.constraints.NotNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GameReadyService(
    val gameReadyRepository: GameReadyRepository
) {
    @Transactional
    fun updateGameReadyStatus(memberSn: Long, roomSn: Long, gameType: GameType, @NotNull readyStatus: ReadyStatus) : GameReady {

        return gameReadyRepository.findGameReadyByMemberSnAndRoomSnAndGameType(memberSn, roomSn, gameType)?.let {
            it.updateReadyStatus(readyStatus)
            it
        }?: throw Exception("게임 준비 상태가 존재하지 않습니다.")
    }

    fun getGameReadyStatusOfRoomMember(roomSn: Long, gameType: GameType): List<GameReady> {
        return gameReadyRepository.findGameReadyByRoomSnAndGameType(roomSn, gameType)
    }
}