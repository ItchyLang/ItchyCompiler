package net.itchy.scratch.representation

data class Block
(
    @Transient val id : String,
    val opcode : String,
    var next : String? = null,
    var parent : String? = null,
    val topLevel : Boolean = true,
    val inputs : Map<String, Input> = mapOf(),
    val fields : HashMap<String, Field> = HashMap(),
    val shadow : Boolean = false,
    val x : Int = 0,
    val y : Int = 0,
    val mutation : Mutation = Mutation()
)