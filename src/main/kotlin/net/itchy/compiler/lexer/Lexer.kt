package net.itchy.compiler.lexer

import net.itchy.compiler.CompileException
import net.itchy.compiler.token.Token
import net.itchy.compiler.token.TokenPosition
import net.itchy.compiler.token.TokenType

class Lexer(
    private val text: String,
    private val name: String = "Default"
) {
    companion object {
        val LEXER_CONTEXT = LexerContext()
            // Whitespaces
            .addRule(TokenType.WHITESPACE) { i ->
                i.addRegex("[ \t\r]")
            }
            .addRule(TokenType.NEW_LINE)
            .addRule(TokenType.COMMENT) { i ->
                i.addMultiline("/*", "*/").addRegex("//[^\\r\\n]*")
            }

            // Arithmetics
            .addRule(TokenType.PLUS)
            .addRule(TokenType.MINUS)
            .addRule(TokenType.MULTIPLY)
            .addRule(TokenType.DIVIDE)
            .addRule(TokenType.MODULUS)

            // Atoms
            .addRule(TokenType.IDENTIFIER) { i -> i.addRegex("[a-zA-Z_][a-zA-Z0-9_]*") }
            .addRule(TokenType.STRING) { i -> i.addMultiline("\"", "\\", "\"").addMultiline("'", "\\", "'") }
            .addRule(TokenType.NUMBER) { i -> i.addRegexes("[0-9]+\\.[0-9]+", "[0-9]+", "0[xX][0-9a-fA-F]+") }
            .addRule(TokenType.TRUE)
            .addRule(TokenType.FALSE)

            // Comparisons - This must be defined AFTER identifiers
            .addRule(TokenType.EQUALS)
            .addRule(TokenType.NOT_EQUALS)
            .addRule(TokenType.LT_EQUALS)
            .addRule(TokenType.GT_EQUALS)
            .addRule(TokenType.LT)
            .addRule(TokenType.GT)
            .addRule(TokenType.NOT)
            .addRule(TokenType.AND)
            .addRule(TokenType.OR)

            // Memory operations
            .addRule(TokenType.ASSIGN)
            .addRule(TokenType.PLUS_ASSIGN)
            .addRule(TokenType.MINUS_ASSIGN)
            .addRule(TokenType.MULTIPLY_ASSIGN)
            .addRule(TokenType.DIVIDE_ASSIGN)

            // Brackets
            .addRule(TokenType.LEFT_BRACKET)
            .addRule(TokenType.RIGHT_BRACKET)
            .addRule(TokenType.LEFT_CURLY_BRACKET)
            .addRule(TokenType.RIGHT_CURLY_BRACKET)

            // Delimiters
            .addRule(TokenType.COLON)
            .addRule(TokenType.COMMA)

            // Keywords
            .addRule(TokenType.IF)
            .addRule(TokenType.ELSE)
            .addRule(TokenType.LOOP)
            .addRule(TokenType.FOREVER)
            .addRule(TokenType.COUNT)
            .addRule(TokenType.UNTIL)
            .addRule(TokenType.RETURN)
            .addRule(TokenType.FUNC)
            .addRule(TokenType.FAST)
            .addRule(TokenType.SPRITE)
            .addRule(TokenType.WHEN)
            .addRule(TokenType.LET)
    }

    fun lex(): List<Token> {
        val tokens = ArrayList<Token>()
        val length = this.text.length
        var offset = 0
        var line = 0
        var column = 0
        var input = this.text
        while (offset < length) {
            val token = LEXER_CONTEXT.nextToken(input) ?: throw CompileException(TokenPosition(line, column))
            if (token.length + offset > length) {
                break
            }
            val oldLine = line
            val oldColumn = column
            for (i in offset ..< offset + token.length) {
                val c = this.text[i]
                if (c == '\n') {
                    line++
                    column = 0
                } else {
                    column++
                }
            }

            val final = Token(
                token.type,
                token.content,
                TokenPosition(oldLine, oldColumn)
            )
            tokens.add(final)

            input = input.substring(token.length)
            offset += token.length
        }
        tokens.add(Token(TokenType.EOF, "", TokenPosition(line, column)))
        return tokens
    }
}

