package de.fpolachowski.papercurator.model

import jakarta.persistence.*

@Entity
@Table(name = "LINKS")
data class Link (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val url: String,

    @Column(nullable = false)
    val contentType : ContentType,
) {
    constructor() : this(null, "", ContentType.WEBSITE)
}

enum class ContentType {
    WEBSITE, PDF
}