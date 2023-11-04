import net.itchy.compiler.lexer.Lexer
import net.itchy.compiler.parser.Parser

fun main() {
    val x = Lexer("sprite MySprite { when init { } }").lex()
    val y = Parser(x).parse()
    println(y)
}