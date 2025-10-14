package br.com.example.juscom

import java.io.Serializable

data class StudyMaterial(
    val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val author: String,
    val date: String,
    val imageResource: Int = R.drawable.scales
) : Serializable
