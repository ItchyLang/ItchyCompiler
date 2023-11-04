import assets.loadCostume
import com.google.gson.GsonBuilder
import representation.*
import serialization.*
import util.Either
import util.VariantValue
import java.util.UUID

fun main()
{
    // Setup GSON
    val gson = GsonBuilder()
        .registerTypeAdapter(VariantValue::class.java, VariantValueAdaptor())
        .registerTypeAdapter(Either::class.java, EitherAdaptor())
        .registerTypeAdapter(InputSpec::class.java, InputSpecAdaptor())
        .registerTypeAdapter(Input::class.java, InputAdaptor())
        .registerTypeAdapter(Field::class.java, FieldAdaptor())
        .registerTypeAdapter(Variable::class.java, VariableAdaptor())
        .serializeNulls()
        .setPrettyPrinting()
        .create()

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

    val whenFlagBlock = Block("event_whenflagclicked", setVarID)

    val setVarInput = Input(1, Either.right(InputSpec(10, VariantValue("17"), null)), null)
    val setVarField = Field(VariantValue("mattyVar"), mattyVarID)

    val setVarBlock = Block("data_setvariableto", null, whenFlagID, false, hashMapOf("VALUE" to setVarInput),
        hashMapOf("VARIABLE" to setVarField))

    val stage = Stage(arrayListOf(backdrop), hashMapOf(mattyVarID to mattyVar),
        hashMapOf(whenFlagID to whenFlagBlock, setVarID to setVarBlock))

    // ==== Construct Project ====
    val proj = ScratchProject(arrayListOf(stage), arrayListOf(mattyVarMonitor))

    println(gson.toJson(proj))
}