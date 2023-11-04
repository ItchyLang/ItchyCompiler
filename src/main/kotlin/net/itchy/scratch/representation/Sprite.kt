package net.itchy.scratch.representation

data class Sprite(
    // Member cannot be defaulted
    override val name: String,
    override val costumes: ArrayList<Costume>,

    // Defaults for all members
    override val blocks: HashMap<String, Block> = HashMap(),
    override val variables: HashMap<String, Variable> = HashMap(),
    override val broadcasts: HashMap<String, String> = HashMap(),
    override val sounds: ArrayList<Sound> = arrayListOf(),
    override val currentCostume: Int = 0,
    override val layerOrder: Int = 1,
    override val volume: Int = 100,

    // Sprite specific members
    val visible : Boolean = true,
    val x : Int = 0,
    val y : Int = 0,
    val size : Int = 100,
    val direction : Int = 90,
    val draggable : Boolean = false,
    val rotationStyle : String = "all around"
) : Target()
{
    // Completely forced
    override val isStage: Boolean = false
}