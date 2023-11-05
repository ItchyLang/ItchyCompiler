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
		load_costume("abby-a", "./abby-a.svg")
		load_backdrop("backdrop", "./backdrop.svg")
	}

    func foo_bar(a: string, b: string, c: string, d: boolean) {
        return a + b + c
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