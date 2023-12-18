package com.midasit.mcafe.domain.gamedata

import com.midasit.mcafe.domain.gamedata.QGameReady.gameReady
import com.midasit.mcafe.domain.order.Order
import com.midasit.mcafe.domain.order.QOrder.order
import com.midasit.mcafe.model.GameType
import com.midasit.mcafe.model.ReadyStatus
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


    override fun findAllOrderByRoomSnAndGameTypeAndGameReadyStatus(roomSn: Long, gType: GameType, readyStatus: ReadyStatus): List<Order> {
        return jpaQueryFactory
            .selectFrom(order)
            .leftJoin(gameReady)
            .on(
                order.member.sn.eq(gameReady.member.sn),
                order.room.sn.eq(gameReady.room.sn)
            ).where(
                order.room.sn.eq(roomSn),
                gameReady.gameType.eq(gType),
                gameReady.readyStatus.eq(readyStatus)
            ).fetch()
    }

}