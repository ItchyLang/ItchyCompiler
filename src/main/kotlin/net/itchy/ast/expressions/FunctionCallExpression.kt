package net.itchy.ast.expressions

import net.itchy.ast.ExpressionVisitor
import net.itchy.compiler.token.TokenPosition

data class FunctionCallExpression(
    val name: String,
    val arguments: List<Expression>,
    val position: TokenPosition
): Expression() {
    init {
        this.addParentTo(this.arguments)
    }

    override fun <R> visit(visitor: ExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}