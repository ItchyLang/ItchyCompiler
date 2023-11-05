package net.itchy.ast.statements

import net.itchy.ast.StatementVisitor
import net.itchy.ast.Visitable

abstract class Statement: Visitable() {
    abstract fun <R> visit(visitor: StatementVisitor<R>): R
}