package com.sivalabs.techtube.videos.domain

import com.sivalabs.techtube.users.domain.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "videos")
class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "video_id_generator")
    @SequenceGenerator(
        name = "video_id_generator",
        sequenceName = "video_id_seq",
        allocationSize = 10,
    )
    var id: Long? = null

    @Column(nullable = false)
    var title: String = ""

    @Column(nullable = false)
    var url: String = ""

    @Column(nullable = false)
    var description: String = ""

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    var category: Category? = null

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    var createdBy: User? = null

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: VideoStatus = VideoStatus.PENDING

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
}
