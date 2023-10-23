package com.midasit.mcafe.infra.component.rs.uchef.menu

import com.fasterxml.jackson.annotation.JsonProperty

class SearchResult(
        @JsonProperty("jsonData")
        val jsonData: JsonData
)