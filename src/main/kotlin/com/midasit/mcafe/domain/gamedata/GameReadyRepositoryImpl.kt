package com.midasit.mcafe.domain.gamedata

import com.midasit.mcafe.domain.gamedata.QGameReady.gameReady
import com.midasit.mcafe.model.GameType
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class GameReadyRepositoryImpl(private val jpaQueryFactory: JPAQueryFactory) : GameReadyRepositoryExtend {
    override fun findGameReadyByMemberSnAndRoomSnAndGameType(memberSn: Long, roomSn: Long, gameType: GameType): GameReady?{

        return jpaQueryFactory
            .selectFrom(gameReady)
            .where(
                gameReady.member.sn.eq(memberSn),
                gameReady.room.sn.eq(roomSn),
                gameReady.gameType.eq(gameType)
            ).fetchOne()

    }

    override fun findGameReadyByRoomSnAndGameType(roomSn: Long, gameType: GameType): List<GameReady> {
        return jpaQueryFactory
            .selectFrom(gameReady)
            .where(
                gameReady.room.sn.eq(roomSn),
                gameReady.gameType.eq(gameType)
            ).fetch()
    }

}