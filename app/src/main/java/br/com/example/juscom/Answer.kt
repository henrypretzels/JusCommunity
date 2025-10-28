package br.com.example.juscom

import java.util.Date

data class Answer(
    var id: String = "",
    val questionId: String = "",
    val body: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val timestamp: Date? = null,
    val voteCount: Long = 0
)
