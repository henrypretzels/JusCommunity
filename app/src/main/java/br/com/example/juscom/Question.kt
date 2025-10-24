package br.com.example.juscom

import java.util.Date

data class Question(
    val roomId: String = "",
    val title: String = "",
    val body: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val timestamp: Date? = null,
    val answerCount: Long = 0
)
