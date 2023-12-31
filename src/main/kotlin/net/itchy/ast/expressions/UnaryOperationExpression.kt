package net.itchy.ast.expressions

import net.itchy.ast.ExpressionVisitor
import net.itchy.compiler.token.TokenType

data class UnaryOperationExpression(
    val expression: Expression,
    val operator: TokenType
): Expression() {
    init {
        this.addParentTo(this.expression)
    }

    override fun <R> visit(visitor: ExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}