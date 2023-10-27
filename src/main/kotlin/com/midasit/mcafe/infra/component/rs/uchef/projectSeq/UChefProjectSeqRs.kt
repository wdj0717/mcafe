package com.midasit.mcafe.infra.component.rs.uchef.projectSeq

import com.fasterxml.jackson.annotation.JsonProperty

class UChefProjectSeqRs(
        @JsonProperty("searchResult")
        val searchResult: SearchResult
)