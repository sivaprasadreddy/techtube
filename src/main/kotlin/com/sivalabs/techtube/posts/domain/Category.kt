package com.sivalabs.techtube.posts.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table

@Entity
@Table(name = "categories")
class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "post_id_generator")
    @SequenceGenerator(
        name = "post_id_generator",
        sequenceName = "post_id_seq",
        allocationSize = 10,
    )
    var id: Long? = null

    @Column(unique = true, nullable = false)
    var name: String = ""
}
