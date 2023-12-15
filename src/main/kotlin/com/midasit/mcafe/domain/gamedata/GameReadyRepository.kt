package com.midasit.mcafe.domain.gamedata

import com.midasit.mcafe.domain.looseHistory.LooseHistory
import org.springframework.data.jpa.repository.JpaRepository

interface GameReadyRepository : JpaRepository<LooseHistory, Long>, GameReadyRepositoryExtend {

}