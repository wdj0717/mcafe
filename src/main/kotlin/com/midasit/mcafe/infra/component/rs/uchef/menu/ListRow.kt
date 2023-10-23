package com.midasit.mcafe.infra.component.rs.uchef.menu

import com.fasterxml.jackson.annotation.JsonProperty

class ListRow(
        @JsonProperty("ORDERBUTTONCOMP")
        val orderButtonComp: Array<OrderButtonComp>
)