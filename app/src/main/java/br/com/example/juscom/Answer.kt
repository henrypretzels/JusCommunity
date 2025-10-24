package br.com.example.juscom

import java.util.Date

data class Answer(
    val questionId: String = "",
    val body: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val timestamp: Date? = null,
    val voteCount: Long = 0
)
