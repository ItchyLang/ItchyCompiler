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
		
		let n : double = 2
		loop forever
		{
			let isPrime : boolean = true
			let fac : double = 2
			
			loop until (fac == n)
			{
				if (n % fac == 0)
				{
					isPrime = false	
				}
				fac += 1
			}

			if (isPrime) { say(n) }
			
			n += 1
		}
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