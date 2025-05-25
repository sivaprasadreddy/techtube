package com.sivalabs.techtube

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "techtube")
class ApplicationProperties(
    var youtubeApiKey: String = "",
)
