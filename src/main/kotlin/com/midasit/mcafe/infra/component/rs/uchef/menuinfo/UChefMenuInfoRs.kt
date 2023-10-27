package com.midasit.mcafe.infra.component.rs.uchef.menuinfo

import com.fasterxml.jackson.annotation.JsonProperty

class UChefMenuInfoRs(
        @JsonProperty("searchResult")
        val searchResult: SearchResult
)