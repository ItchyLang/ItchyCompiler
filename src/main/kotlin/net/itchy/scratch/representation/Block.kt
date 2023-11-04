package representation

data class Block
(
    val opcode : String,


    var next : String? = null,
    var parent : String? = null,
    val topLevel : Boolean = true,
    val inputs : HashMap<String, Input> = HashMap(),
    val fields : HashMap<String, Field> = HashMap(),
    val shadow : Boolean = false,
    val x : Int = 0,
    val y : Int = 0,
    val mutation : Mutation = Mutation()
)