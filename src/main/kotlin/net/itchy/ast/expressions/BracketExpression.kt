package net.itchy.ast.expressions

import net.itchy.ast.ExpressionVisitor

data class BracketExpression(
    val expression: Expression
): Expression() {
    init {
        this.expression.parent = this.parent
    }

    override fun <R> visit(visitor: ExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}