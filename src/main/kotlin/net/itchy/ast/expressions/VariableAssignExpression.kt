package net.itchy.ast.expressions

import net.itchy.ast.ExpressionVisitor

data class VariableAssignExpression(
    val name: String,
    val assignee: Expression
): Expression() {
    override fun <R> visit(visitor: ExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}