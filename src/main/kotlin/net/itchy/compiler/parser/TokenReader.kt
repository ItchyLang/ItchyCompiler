package net.itchy.compiler.parser

import net.itchy.compiler.CompileException
import net.itchy.compiler.token.Token
import net.itchy.compiler.token.TokenPosition
import net.itchy.compiler.token.TokenType

class TokenReader(private val tokens: List<Token>) {
    private var index = 0

    fun advance(amount: Int = 1, newlines: Boolean = false): Token {
        if (newlines) {
            this.index = this.getOffset(amount)
            return this.peek()
        }
        var nonNewLines = 0
        while (nonNewLines != amount) {
            this.index = this.getOffset(1)
            if (this.peek().type != TokenType.NEW_LINE) {
                nonNewLines++
            }
        }
        return this.peek()
    }

    fun recede(amount: Int = 1, newlines: Boolean = false): Token {
        return this.advance(-amount, newlines)
    }

    fun position(amount: Int = 0): TokenPosition {
        return this.peek(amount).position
    }

    fun peek(amount: Int = 0): Token {
        return this.tokens[this.getOffset(amount)]
    }

    fun type(amount: Int = 0): TokenType {
        return this.peek(amount).type
    }

    fun isType(vararg types: TokenType): Boolean {
        val type = this.type()
        return types.contains(type)
    }

    fun match(vararg types: TokenType, newlines: Boolean = false): Token? {
        val token = this.peek()
        if (token.type in types) {
            this.advance(newlines = newlines)
            return token
        }
        return null
    }

    fun isMatch(vararg types: TokenType, newlines: Boolean = false): Boolean {
        return this.match(*types, newlines = newlines) != null
    }

    fun isAtEnd(): Boolean {
        return this.type() == TokenType.EOF
    }

    fun isInBounds(offset: Int): Boolean {
        val offsetIndex = this.index + offset
        return offsetIndex < this.tokens.size && offsetIndex >= 0
    }

    fun mustMatch(
        vararg types: TokenType,
        newlines: Boolean = false,
        message: (Token) -> String = { "Expected type(s) ${types.contentToString()} at ${it.position}" }
    ): Token {
        var token = this.match(*types, newlines = newlines)
        if (token == null) {
            token = this.peek()
            throw CompileException(token.position, message(token))
        }
        return token
    }

    private fun getOffset(offset: Int): Int {
        val offsetIndex = this.index + offset
        require(offsetIndex < this.tokens.size && offsetIndex >= 0) { "Index $offsetIndex is out of bounds" }
        return offsetIndex
    }
}