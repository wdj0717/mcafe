package com.midasit.mcafe.infra.component.rs.uchef.menu

import com.fasterxml.jackson.annotation.JsonProperty

class JsonData(
        @JsonProperty("PAGELIST")
        val pageList: PageList
)