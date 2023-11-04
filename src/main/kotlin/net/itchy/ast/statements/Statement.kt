package net.itchy.ast.statements

import net.itchy.ast.StatementVisitor
import net.itchy.ast.Visitable

abstract class Statement: Visitable() {
    var previous: Statement? = null
    var next: Statement? = null

    abstract fun <R> visit(visitor: StatementVisitor<R>): R
}