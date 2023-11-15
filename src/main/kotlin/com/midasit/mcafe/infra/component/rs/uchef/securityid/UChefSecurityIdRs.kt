package com.midasit.mcafe.infra.component.rs.uchef.securityid

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
class UChefSecurityIdRs(
    @JsonProperty("searchResult")
    val searchResult: SearchResult?,
)

class SearchResult(
    @JsonProperty("security_id")
    val securityId: String
)