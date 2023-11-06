package net.itchy.scratch.representation

class ScratchProject {
    val targets: MutableList<Target> = ArrayList()
    val extensions: MutableList<String> = ArrayList()

    private val meta = Metadata()
    private val monitors = listOf<Nothing>()
}