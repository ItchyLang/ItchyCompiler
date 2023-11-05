package net.itchy.ast.statements

import net.itchy.ast.StatementVisitor
import net.itchy.ast.expressions.Expression
import net.itchy.ast.expressions.FunctionCallExpression
import net.itchy.compiler.token.TokenPosition

data class FunctionCallStatement(
    val expression: FunctionCallExpression,
    val position: TokenPosition
): Statement() {
    init {
        this.addParentTo(this.expression)
    }

    override fun <R> visit(visitor: StatementVisitor<R>): R {
        return visitor.visit(this)
    }
}