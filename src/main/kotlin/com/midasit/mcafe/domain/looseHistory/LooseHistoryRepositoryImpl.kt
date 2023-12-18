package com.midasit.mcafe.domain.looseHistory

import com.midasit.mcafe.domain.looseHistory.dto.LooseHistoryDto
import com.midasit.mcafe.model.GameType
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

import com.midasit.mcafe.domain.looseHistory.QLooseHistory.looseHistory
import com.midasit.mcafe.domain.member.QMember.member
import com.querydsl.jpa.JPAExpressions

@Repository
class LooseHistoryRepositoryImpl(private val jpaQueryFactory: JPAQueryFactory) : LooseHistoryRepositoryExtend {

    override fun findLooseHistoryDtoList(looserSnList: List<Long>, roomSn: Long, gType: GameType, startLooseDate: LocalDateTime, endLooseDate: LocalDateTime): List<LooseHistoryDto> {
        val looseCountSubQuery = JPAExpressions
            .select(looseHistory.count())
            .from(looseHistory)
            .where(
                looseHistory.looser.eq(member),
                looseHistory.room.sn.eq(roomSn),
                looseHistory.gameType.eq(gType),
                looseHistory.looseDate.between(startLooseDate, endLooseDate)
            )

        return jpaQueryFactory
            .select(
                Projections.constructor(
                    LooseHistoryDto::class.java,
                    member,
                    looseHistory.gameType,
                    looseCountSubQuery
                )
            )
            .from(looseHistory)
            .join(looseHistory.looser, member)
            .where(
                looseHistory.looser.sn.`in`(looserSnList),
                looseHistory.room.sn.eq(roomSn),
                looseHistory.gameType.eq(gType),
                looseHistory.looseDate.between(startLooseDate, endLooseDate)
            )
            .groupBy(looseHistory.looser, looseHistory.gameType)
            .fetch()
    }

}