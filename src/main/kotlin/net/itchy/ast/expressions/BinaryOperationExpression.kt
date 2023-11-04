package net.itchy.ast.expressions

import net.itchy.ast.ExpressionVisitor
import net.itchy.compiler.token.TokenType

data class BinaryOperationExpression(
    val left: Expression,
    val right: Expression,
    val operator: TokenType
): Expression() {
    init {
        this.addParentTo(this.left)
        this.addParentTo(this.right)
    }

    override fun <R> visit(visitor: ExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}