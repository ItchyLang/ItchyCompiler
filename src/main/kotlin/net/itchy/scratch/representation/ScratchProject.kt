package net.itchy.scratch.representation

data class ScratchProject
(
    var targets : ArrayList<Target>,
    val monitors : ArrayList<Monitor> = arrayListOf(),
    var extensions : ArrayList<String> = arrayListOf()
)
{
    var meta : Metadata = Metadata()
}