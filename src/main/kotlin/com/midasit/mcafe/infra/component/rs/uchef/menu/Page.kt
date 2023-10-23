package com.midasit.mcafe.infra.component.rs.uchef.menu

import com.fasterxml.jackson.annotation.JsonProperty

class Page(
        @JsonProperty("name")
        val name: String,
        @JsonProperty("LISTCOMP")
        val listComp: ListComp
)