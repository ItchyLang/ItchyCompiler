import net.itchy.compiler.lexer.Lexer
import net.itchy.compiler.parser.Parser

fun main() {
    val x = Lexer(
        """
        when init {
            true && (false || false)
        }
        """.trimIndent()
    ).lex()

    val y = Parser(x).parse()
    println(y)
}