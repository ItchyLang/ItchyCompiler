package net.itchy.compiler.lexer

import net.itchy.compiler.token.TokenType

/**
 * Context that stores all our lexing rules
 */
class LexerContext {
    private val rules = ArrayList<LexerRule>()

    fun addRule(type: TokenType): LexerContext {
        val rule = LexerRule(type)
        rule.addString(type.asString())
        this.rules.add(rule)
        return this
    }

    fun addRule(type: TokenType, consumer: (LexerRule) -> Unit): LexerContext {
        val rule = LexerRule(type)
        consumer(rule)
        this.rules.add(rule)
        return this
    }

    fun nextToken(input: String): LexerToken? {
        var selectedRule: LexerRule? = null
        var longestRule = 1
        for (rule in this.rules) {
            val length = rule.getMatchLength(input)
            if (length >= longestRule) {
                longestRule = length
                selectedRule = rule
            }
        }
        return selectedRule?.let { LexerToken(selectedRule.type, input.substring(0, longestRule)) }
    }
}