package com.sivalabs.techtube.users.domain

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table

@Entity
@Table(name = "users")
class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_generator")
    @SequenceGenerator(
        name = "user_id_generator",
        sequenceName = "user_id_seq",
        allocationSize = 10,
    )
    var id: Long? = null
    var name: String = ""
    var email: String = ""
    var password: String = ""

    @Enumerated(EnumType.STRING)
    var role: Role = Role.ROLE_USER
}
