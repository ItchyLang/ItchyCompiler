package net.itchy.scratch.representation

sealed class Target {
    abstract val isStage: Boolean
    abstract val name: String
    abstract val layerOrder: Int
    abstract val lists: Map<String, ScratchList>

    // These must be mutable we add to these later
    val costumes: MutableList<Costume> = ArrayList()
    val sounds: MutableList<Sound> = ArrayList()
    val variables: MutableMap<String, Variable> = HashMap()
    val broadcasts: MutableMap<String, String> = HashMap()
    val blocks: MutableMap<String, Block> = HashMap()

    // We do not add comments
    val comments: Map<Nothing, Nothing> = mapOf()
    val currentCostume = 0
    val volume = 100
}