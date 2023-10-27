package com.midasit.mcafe.infra.component.rs.uchef.projectSeq

import com.fasterxml.jackson.annotation.JsonProperty

class SearchResult (
        @JsonProperty("memberData")
        val memberData: MemberData
)