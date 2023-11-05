import net.itchy.compiler.lexer.Lexer
import net.itchy.compiler.parser.Parser
import net.itchy.scratch.ScratchGenerator
import net.itchy.scratch.bundling.bundle

fun main()
{
    val src = """
        sprite X
        {
            when init
            {
                load_costume("abby-a", "C:\\Users\\matty\\Desktop\\HelloWorldTest\\abby-a.svg")
                load_backdrop("backdrop", "C:\\Users\\matty\\Desktop\\HelloWorldTest\\backdrop.svg")
                say(1 + 2 + 3)
            }
        }
        """.trimIndent()

    // Lexing
    val tokens = Lexer(src).lex()

    // Parsing
    val ast = Parser(tokens).parse()

    // Generating
    val project = ScratchGenerator().generate(ast)

    // Serialization and Bundling
    bundle("SaySum", project, ".")
}