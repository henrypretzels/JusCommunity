package br.com.example.juscom

import com.google.firebase.Timestamp
import java.io.Serializable

data class StudyMaterial(
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val authorName: String = "",
    val fileUrl: String? = null,
    val timestamp: Timestamp? = null
) : Serializable
