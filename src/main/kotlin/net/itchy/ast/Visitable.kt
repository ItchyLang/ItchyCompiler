package net.itchy.ast

abstract class Visitable {
    val id by lazy { this.hashCode().toString(16) }

    lateinit var parent: Visitable

    fun addParentTo(visitables: Collection<Visitable>) {
        for (visitable in visitables) {
            this.addParentTo(visitable)
        }
    }

    fun addParentTo(visitable: Visitable) {
        visitable.parent = this
    }
}