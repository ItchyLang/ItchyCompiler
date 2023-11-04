package representation

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

    // Completely forced
    val lists : HashMap<Int, Int> = HashMap()
    val comments : HashMap<Int, Int> = HashMap()
}