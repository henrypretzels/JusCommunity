package br.com.example.juscom

data class Badge(
    val name: String = "",
    val description: String = "",
    val iconUrl: String = "",
    val criteria: Map<String, @JvmWildcard Any> = emptyMap()
)
