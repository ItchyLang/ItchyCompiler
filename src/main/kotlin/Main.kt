import com.google.gson.GsonBuilder
import net.itchy.compiler.lexer.Lexer
import net.itchy.compiler.parser.Parser
import net.itchy.scratch.ScratchGenerator
import net.itchy.scratch.bundling.bundle
import net.itchy.scratch.representation.*
import net.itchy.scratch.serialization.*
import net.itchy.utils.Either
import net.itchy.utils.VariantValue

fun main() {
    /*
    // ====== Construct Scratch Project ======
    val assetPath = "C:\\Users\\matty\\Desktop\\Scratch Examples\\Fields"

    // ==== Construct stage ====
    val backdrop = loadCostume("backdrop1", "$assetPath\\cd21514d0531fdffb22204e0ec5ed84a.svg", 480, 360)
    val mattyVarID = UUID.randomUUID().toString()
    val mattyVar = Variable("mattyVar", VariantValue(0.0))
    val mattyVarMonitor = Monitor(false, mattyVarID, "mattyVar", null)

    // BLOCKS
    // Block IDs
    val whenFlagID = UUID.randomUUID().toString()
    val setVarID = UUID.randomUUID().toString()

    val whenFlagBlock = Block(
        id = whenFlagID,
        opcode = "event_whenflagclicked",
        next = setVarID
    )

    val setVarInput = Input(1, Either.right(InputSpec(10, VariantValue("17"), null)), null)
    val setVarField = Field(VariantValue("mattyVar"), mattyVarID)

    val setVarBlock = Block(
        id = setVarID,
        opcode = "data_setvariableto",
        next = null,
        parent = whenFlagID,
        topLevel = false,
        inputs = hashMapOf("VALUE" to setVarInput),
        fields = hashMapOf("VARIABLE" to setVarField)
    )

    val stage = Stage(arrayListOf(backdrop), hashMapOf(mattyVarID to mattyVar),
        hashMapOf(whenFlagID to whenFlagBlock, setVarID to setVarBlock))

    // ==== Construct Project ====
    val proj = ScratchProject(arrayListOf(stage), arrayListOf(mattyVarMonitor))

    println(gson.toJson(proj))
    */

    val tokens = Lexer("sprite X { when init { say(1 + 2) } }").lex()

    val ast = Parser(tokens).parse()
    println(ast)

    val generator = ScratchGenerator()
    for (statement in ast) {
        statement.visit(generator)
    }
    bundle("SaySum", generator.scratchProject, ".")
}