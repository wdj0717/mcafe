package com.midasit.mcafe.domain.looseHistory

import org.springframework.data.jpa.repository.JpaRepository

interface LooseHistoryRepository : JpaRepository<LooseHistory, Long>, LooseHistoryRepositoryExtend {

}