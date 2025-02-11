package de.fpolachowski.papercurator.model

import jakarta.persistence.*

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
    @Column(nullable = false)
    val content : String,
    @Column(nullable = false)
    val shortDescription : String,
    @Column(nullable = false)
    val description : String,
) {
    constructor() : this(null, "", "", "", "", "")
}