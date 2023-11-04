package net.itchy.compiler.lexer

import net.itchy.compiler.token.TokenType
import net.itchy.utils.StringUtils.escapeForRegex
import java.util.regex.Pattern

class LexerRule(val type: TokenType) {
    private val matches = ArrayList<Pattern>()

    fun addString(value: String): LexerRule {
        this.matches.add(Pattern.compile(value.escapeForRegex()))
        return this
    }

    fun addRegex(regex: String): LexerRule {
        this.matches.add(Pattern.compile(regex))
        return this
    }

    fun addRegexes(vararg regexes: String): LexerRule {
        for (regex in regexes) {
            this.addRegex(regex)
        }
        return this
    }

    fun addMultiline(open: String, close: String): LexerRule {
        return this.addMultiline(open, "", close)
    }

    fun addMultiline(open: String, escape: String, close: String): LexerRule {
        val s: String = open.escapeForRegex()
        val c: String = close.escapeForRegex()
        val regex: String = if (escape.isEmpty()) "$s.*?$c" else {
            val e: String = escape.escapeForRegex()
            "$s(?:$e(?:$e|$c|(?!$c).)|(?!$e|$c).)*$c"
        }
        this.matches.add(Pattern.compile(regex, Pattern.DOTALL))
        return this
    }

    fun getMatchLength(string: String): Int {
        var length = 0
        for (pattern in this.matches) {
            val matcher = pattern.matcher(string)
            if (matcher.lookingAt()) {
                length = length.coerceAtLeast(matcher.end())
            }
        }
        return if (length < 1) -1 else length
    }
}