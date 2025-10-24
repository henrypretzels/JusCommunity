package br.com.example.juscom

data class HelpItem(
    val question: String = "",
    val answer: String = "",
    val order: Int = 0,
    var isExpanded: Boolean = false
)
