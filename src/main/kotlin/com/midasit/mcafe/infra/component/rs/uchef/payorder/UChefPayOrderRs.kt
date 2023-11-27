package com.midasit.mcafe.infra.component.rs.uchef.payorder

import com.fasterxml.jackson.annotation.JsonProperty

class UChefPayOrderRs(
    @JsonProperty("searchResult")
    val searchResult: SearchResult,
    @JsonProperty("resultCode")
    val resultCode: String
)

class SearchResult(
    @JsonProperty("order_no")
    val orderNo: String
)