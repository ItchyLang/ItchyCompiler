import net.itchy.compiler.lexer.Lexer

fun main() {
    val x = Lexer("sprite MySprite { when init { } }").lex()
    println(x)
}