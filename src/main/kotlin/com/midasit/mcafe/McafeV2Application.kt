package com.midasit.mcafe

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class McafeV2Application

fun main(args: Array<String>) {
	runApplication<McafeV2Application>(*args)
}
