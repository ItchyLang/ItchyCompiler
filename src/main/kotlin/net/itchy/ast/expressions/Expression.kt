package net.itchy.ast.expressions

import net.itchy.ast.ExpressionVisitor
import net.itchy.ast.Visitable

abstract class Expression: Visitable() {
    abstract fun <R> visit(visitor: ExpressionVisitor<R>): R
}