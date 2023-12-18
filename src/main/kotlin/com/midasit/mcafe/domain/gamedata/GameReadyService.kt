package com.midasit.mcafe.domain.gamedata

import com.midasit.mcafe.infra.exception.CustomException
import com.midasit.mcafe.infra.exception.ErrorMessage
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

        return gameReadyRepository.findGameReadyByMemberSnAndRoomSnAndGameType(memberSn, roomSn, gameType)?.also {
            it.updateReadyStatus(readyStatus)
        } ?: throw CustomException(ErrorMessage.NO_GAME_READY_STATUS)
    }

    fun getGameReadyStatusOfRoomMember(roomSn: Long, gameType: GameType): List<GameReady> {
        return gameReadyRepository.findGameReadyByRoomSnAndGameType(roomSn, gameType)
    }
}