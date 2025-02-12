package de.fpolachowski.papercurator.model

import jakarta.persistence.*

@Entity
@Table(name = "authors")
data class Author (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id : Long? = null,
    @Column(nullable = false)
    val name : String,
) {
    constructor() : this(null, "") {

    }
}