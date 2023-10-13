package com.midasit.mcafe.infra.component

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class UChefComponent(private val webClient: WebClient,
                     @Value("\${u-chef.domain}")
                     private val uChefDomain: String) {


    private fun createUChefClient(): WebClient {
        return webClient.mutate().baseUrl(uChefDomain).build();
    }

}
