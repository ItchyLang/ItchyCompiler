package net.itchy.compiler.token

data class Token(
    val type: TokenType,
    val content: String,
    val position: TokenPosition
)