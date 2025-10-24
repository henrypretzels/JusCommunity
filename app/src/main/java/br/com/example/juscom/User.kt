package br.com.example.juscom

data class User(
    val name: String = "",
    val email: String = "",
    val uf: String = "",
    val institution: String = "",
    val level: Long = 0,
    val points: Long = 0,
    val theme_preference: String = "light"
)
