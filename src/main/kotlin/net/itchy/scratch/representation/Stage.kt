package net.itchy.scratch.representation

data class Stage
(
    override val costumes: ArrayList<Costume>,

    override val variables: HashMap<String, Variable> = HashMap(),
    override val blocks: HashMap<String, Block> = HashMap(),
    override val broadcasts: HashMap<String, String> = HashMap(),
    override val sounds: ArrayList<Sound> = arrayListOf(),
) : Target()
{
    // Completely forced
    override val isStage: Boolean = true
    override val name: String = "Stage"
    val textToSpeechLanguage : String? = null
    val tempo : Int = 60
    val videoState : String = "off"
    val videoTransparency : Int = 50
    override val layerOrder: Int = 0
    override val currentCostume: Int = 0
    override val volume: Int = 100
}