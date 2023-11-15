package com.midasit.mcafe

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class McafeV2Application

fun main(args: Array<String>) {
    runApplication<McafeV2Application>(*args)
}
