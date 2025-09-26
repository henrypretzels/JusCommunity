package com.example.juscom

import java.io.Serializable

data class Room(
    val id: Int,
    val name: String,
    val description: String,
    val category: String,
    val subscribersCount: Int,
    val isSubscribed: Boolean = false,
    val imageResource: Int = R.drawable.scales
) : Serializable

data class Question(
    val id: Int,
    val question: String,
    val answer: String,
    val author: String,
    val date: String,
    val likes: Int = 0
) : Serializable
