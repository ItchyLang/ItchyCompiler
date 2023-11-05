import com.google.gson.GsonBuilder
import net.itchy.compiler.lexer.Lexer
import net.itchy.compiler.parser.Parser
import net.itchy.scratch.ScratchGenerator
import net.itchy.scratch.bundling.bundle
import net.itchy.scratch.representation.*
import net.itchy.scratch.serialization.*
import net.itchy.utils.Either
import net.itchy.utils.VariantValue

fun main()
{
    val tokens = Lexer("""
        sprite X
        {
            when init
            {
                load_costume("abby-a", "C:\\Users\\matty\\Desktop\\HelloWorldTest\\abby-a.svg")
                load_backdrop("backdrop", "C:\\Users\\matty\\Desktop\\HelloWorldTest\\backdrop.svg")
                say(1 + 2)
            }
        }
        """.trimIndent()).lex()

    val ast = Parser(tokens).parse()
    println(ast)

    val generator = ScratchGenerator()
    for (statement in ast) {
        statement.visit(generator)
    }
    bundle("SaySum", generator.scratchProject, ".")
}