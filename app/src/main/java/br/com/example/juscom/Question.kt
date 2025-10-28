package br.com.example.juscom

import com.google.firebase.firestore.DocumentId
import java.io.Serializable
import java.util.Date

data class Question(
    @DocumentId
    var id: String = "",

    val roomId: String = "",
    val title: String = "",
    val body: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val timestamp: Date? = null,
    val answerCount: Long = 0
) : Serializable
