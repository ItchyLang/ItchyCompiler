package net.itchy.ast.expressions

import net.itchy.ast.ExpressionVisitor

data class NumberLiteralExpression(
    val number: Double
): Expression() {
    override fun <R> visit(visitor: ExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}