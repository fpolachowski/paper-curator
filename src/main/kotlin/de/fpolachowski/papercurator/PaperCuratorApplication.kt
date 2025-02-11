package de.fpolachowski.papercurator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PaperCuratorApplication

fun main(args: Array<String>) {
    runApplication<PaperCuratorApplication>(*args)
}
