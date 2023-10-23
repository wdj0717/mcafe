package com.midasit.mcafe.infra.component.rs.uchef.menu

import com.fasterxml.jackson.annotation.JsonProperty

class ListComp(
        @JsonProperty("LISTROW")
        val listRow: Array<ListRow>
)