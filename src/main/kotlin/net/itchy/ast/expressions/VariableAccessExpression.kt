package net.itchy.ast.expressions

import net.itchy.ast.ExpressionVisitor
import net.itchy.compiler.token.TokenPosition

data class VariableAccessExpression(
    val name: String,
    val position: TokenPosition
): Expression() {
    override fun <R> visit(visitor: ExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}