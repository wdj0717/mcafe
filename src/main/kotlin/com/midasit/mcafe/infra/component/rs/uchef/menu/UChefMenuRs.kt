package com.midasit.mcafe.infra.component.rs.uchef.menu

import com.fasterxml.jackson.annotation.JsonProperty

class UChefMenuRs(
        @JsonProperty("searchResult")
        val searchResult: SearchResult
)
