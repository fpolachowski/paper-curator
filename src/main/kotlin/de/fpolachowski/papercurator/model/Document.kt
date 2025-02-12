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

    @ElementCollection(fetch = FetchType.LAZY)
    val urls : List<String>,

    @OneToMany(fetch = FetchType.LAZY, targetEntity = Author::class, cascade = [CascadeType.ALL])
    @JoinColumn(name = "authors")
    val authors : List<Author>,

    @Column(nullable = false)
    val content : String,

    @Column(nullable = false)
    val shortDescription : String,

    @Column(nullable = false)
    val description : String,

    @Column(nullable = false)
    val date : LocalDateTime = LocalDateTime.now(),

    @ElementCollection(fetch = FetchType.LAZY)
    val categories : List<String>
) {
    constructor() : this(null, "", listOf(), listOf(), "", "", "", LocalDateTime.now(), listOf())
}