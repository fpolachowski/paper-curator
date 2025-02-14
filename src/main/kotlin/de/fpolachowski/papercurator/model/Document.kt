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
    val title : String = "",

    @OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @JoinColumn(name = "links")
    val urls : List<Link> = listOf(),

    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(name = "authors")
    val authors : List<Author> = listOf(),

    @Column(nullable = false, columnDefinition = "TEXT")
    val shortDescription : String = "",

    @Column(nullable = false, columnDefinition = "TEXT")
    val description : String = "",

    @Column(nullable = false)
    val date : LocalDateTime = LocalDateTime.now(),

    @ElementCollection(fetch = FetchType.LAZY)
    val categories : List<String> = listOf()
) {
    constructor() : this(null, "", listOf(), listOf(), "", "", LocalDateTime.now(), listOf())
}