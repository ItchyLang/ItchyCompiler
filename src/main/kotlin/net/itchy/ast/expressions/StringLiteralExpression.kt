package net.itchy.ast.expressions

import net.itchy.ast.ExpressionVisitor

data class StringLiteralExpression(
    val literal: String
): Expression() {
    override fun <R> visit(visitor: ExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}