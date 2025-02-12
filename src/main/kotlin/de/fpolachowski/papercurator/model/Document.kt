package de.fpolachowski.papercurator.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "DOCUMENTS")
data class Document(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id : Long? = null,

    @Column(nullable = false)
    val title : String,

    @Column(nullable = false)
    val url : String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authors", nullable = false)
    val authors : Array<Author>,

    @Column(nullable = false)
    val content : String,

    @Column(nullable = false)
    val shortDescription : String,

    @Column(nullable = false)
    val description : String,

    @Column(nullable = false)
    val date : LocalDateTime = LocalDateTime.now()
) {
    constructor() : this(null, "", "", arrayOf(), "", "", "")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Document

        return id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}