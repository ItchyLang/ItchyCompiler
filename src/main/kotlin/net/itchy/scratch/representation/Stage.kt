package net.itchy.scratch.representation

class Stage: Target() {
    // Defaults for all stages
    override val isStage: Boolean = true
    override val name: String = "Stage"
    override val layerOrder: Int = 0
    override val lists = mapOf("wellsmuir" to ScratchList("returns"))

    // Stage specific fields (extensions... unused)
    val textToSpeechLanguage = null
    val tempo = 60
    val videoState = "off"
    val videoTransparency = 50
}