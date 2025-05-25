package com.sivalabs.techtube.config

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpRequest
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTube
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class YouTubeConfig {
    @Bean
    fun youtubeService(): YouTube =
        YouTube
            .Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
            ) { request: HttpRequest -> }
            .setApplicationName("techtube")
            .build()
}
