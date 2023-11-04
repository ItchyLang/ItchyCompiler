package net.itchy

import net.itchy.compiler.CompileException
import net.itchy.compiler.lexer.Lexer
import net.itchy.compiler.parser.Parser
import org.junit.jupiter.api.assertThrows

object TestHelper {
    fun compiles(code: String) {
        Parser(Lexer(code).lex()).parse()
    }

    fun doesntCompile(code: String) {
        assertThrows<CompileException> {
            compiles(code)
        }
    }
}