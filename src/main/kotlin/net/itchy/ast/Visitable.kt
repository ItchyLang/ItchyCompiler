package net.itchy.ast

abstract class Visitable {
    val id: String = this.hashCode().toString(16)

    lateinit var parent: Visitable
}