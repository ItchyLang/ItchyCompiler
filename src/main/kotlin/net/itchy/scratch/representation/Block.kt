package net.itchy.scratch.representation

data class Block(
    @Transient val id : String,
    val opcode: String,
    val topLevel: Boolean,
    val inputs: Map<String, Input> = mapOf(),
    val fields: Map<String, Field> = mapOf(),
    val shadow: Boolean = false,
    val mutation: Mutation = Mutation()
) {
    // Blocks need to be linked properly
    var next: String? = null
    var parent: String? = null

    // We don't care where the blocks generate
    private val x = 0
    private val y = 0
}