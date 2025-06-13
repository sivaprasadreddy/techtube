package com.sivalabs.techtube

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@ConfigurationPropertiesScan
@SpringBootApplication class TechTubeApplication

fun main(args: Array<String>) {
    runApplication<TechTubeApplication>(*args)
}
