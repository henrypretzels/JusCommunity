package br.com.example.juscom

import com.google.firebase.firestore.DocumentId
import java.io.Serializable

// This data class now mirrors the structure in Firestore
// Default values are provided for Firestore's automatic data mapping
data class Room(
    @DocumentId // This annotation automatically maps the document ID from Firestore
    var id: String = "",

    val name: String = "",
    val description: String = "",
    val category: String = "",
    val subscribersCount: Int = 0,
    var isSubscribed: Boolean = false // This will be handled locally, not in Firestore
) : Serializable


// No changes needed for the Question data class at this time
data class Question(
    val id: Int,
    val question: String,
    val answer: String,
    val author: String,
    val date: String,
    val likes: Int = 0
) : Serializable
