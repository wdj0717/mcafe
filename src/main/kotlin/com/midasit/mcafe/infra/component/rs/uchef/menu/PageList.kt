package com.midasit.mcafe.infra.component.rs.uchef.menu

import com.fasterxml.jackson.annotation.JsonProperty

class PageList(
        @JsonProperty("PAGE")
        val page: Array<Page>
)