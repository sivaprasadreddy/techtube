package com.sivalabs.techtube.videos.domain

import com.google.api.services.youtube.YouTube
import com.sivalabs.techtube.ApplicationProperties
import org.springframework.stereotype.Service

@Service
class YouTubeApi(
    private val youtubeService: YouTube,
    private val applicationProperties: ApplicationProperties,
) {
    fun getVideoDetails(videoId: String): YouTubeVideo? {
        if (applicationProperties.youtubeApiKey.isEmpty()) {
            return null
        }
        val detailsRequest: YouTube.Videos.List =
            youtubeService
                .videos()
                .list("snippet,contentDetails")
                .setId(videoId)
                .setKey(applicationProperties.youtubeApiKey)

        val detailsResponse = detailsRequest.execute()
        if (detailsResponse.items.isEmpty()) {
            return null
        }
        detailsResponse.items.first().let {
            val id = it.id
            val snippet = it.snippet
            val title = snippet.title
            val description = snippet.description
            val url = "https://www.youtube.com/watch?v=$id"
            return YouTubeVideo(id, title, url, description)
        }
    }
}
