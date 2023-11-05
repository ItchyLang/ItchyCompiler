package net.itchy.ast.statements

import net.itchy.ast.StatementVisitor
import net.itchy.ast.expressions.Expression
import net.itchy.compiler.token.TokenPosition

data class ReturnStatement(
    val returnExpression: Expression,
    val position: TokenPosition
): Statement() {
    init {
        this.addParentTo(this.returnExpression)
    }

    override fun <R> visit(visitor: StatementVisitor<R>): R {
        return visitor.visit(this)
    }
}