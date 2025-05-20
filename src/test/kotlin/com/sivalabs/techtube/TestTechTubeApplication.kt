package com.sivalabs.techtube

import org.springframework.boot.fromApplication
import org.springframework.boot.with

fun main(args: Array<String>) {
    fromApplication<TechTubeApplication>().with(TestcontainersConfig::class).run(*args)
}
