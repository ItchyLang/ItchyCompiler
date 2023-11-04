package net.itchy.compiler.lexer

import net.itchy.compiler.token.TokenType

data class LexerToken(val type: TokenType, val content: String) {
    val length: Int = this.content.length
}