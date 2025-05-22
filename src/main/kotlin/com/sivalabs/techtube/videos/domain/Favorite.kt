package com.sivalabs.techtube.videos.domain

import com.sivalabs.techtube.users.domain.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "favorites")
class Favorite {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "favorite_id_generator")
    @SequenceGenerator(
        name = "favorite_id_generator",
        sequenceName = "favorite_id_seq",
        allocationSize = 10,
    )
    var id: Long? = null

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    var user: User? = null

    @ManyToOne
    @JoinColumn(name = "video_id", nullable = false)
    var video: Video? = null

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
}
