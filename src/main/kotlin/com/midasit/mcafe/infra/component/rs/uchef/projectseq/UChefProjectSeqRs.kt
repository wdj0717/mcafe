package com.midasit.mcafe.infra.component.rs.uchef.projectseq

import com.fasterxml.jackson.annotation.JsonProperty

class UChefProjectSeqRs(
    @JsonProperty("searchResult")
    val searchResult: SearchResult
)

class SearchResult(
    @JsonProperty("memberData")
    val memberData: MemberData
)

class MemberData(
    @JsonProperty("default_project_seq")
    val defaultProjectSeq: Long,
    @JsonProperty("last_update")
    val lastUpdate: Long
)
