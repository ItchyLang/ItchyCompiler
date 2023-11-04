package net.itchy.ast

import java.util.UUID

abstract class Visitable {
    val id by lazy { UUID.randomUUID().toString() }

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