package net.itchy.scratch.representation

data class Sprite(
    // Member cannot be defaulted
    override val name: String
): Target() {
    // Defaults for all sprites
    override val isStage: Boolean = false
    override val layerOrder: Int = 1
    override val lists: Map<String, ScratchList> = mapOf()

    // Sprite-specific members
    val visible = true
    val x = 0
    val y = 0
    val size = 100
    val direction = 90
    val draggable = false
    val rotationStyle = "all around"
}