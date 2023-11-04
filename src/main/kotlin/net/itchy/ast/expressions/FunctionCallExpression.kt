package net.itchy.ast.expressions

import net.itchy.ast.ExpressionVisitor

data class FunctionCallExpression(
    val name: String,
    val arguments: List<Expression>
): Expression() {
    init {
        this.addParentTo(this.arguments)
    }

    override fun <R> visit(visitor: ExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}