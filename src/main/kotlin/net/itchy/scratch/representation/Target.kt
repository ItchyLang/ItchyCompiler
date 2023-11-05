package net.itchy.scratch.representation

abstract class Target
{
    abstract val isStage : Boolean
    abstract val name : String
    abstract val variables : HashMap<String, Variable>
    abstract val broadcasts : HashMap<String, String>
    abstract val blocks : HashMap<String, Block>
    abstract val currentCostume : Int
    abstract val costumes : ArrayList<Costume>
    abstract val sounds : ArrayList<Sound>
    abstract val layerOrder : Int
    abstract val volume : Int
    abstract val lists : HashMap<String, ScratchList>

    // Completely forced
    val comments : HashMap<Int, Int> = HashMap()
}