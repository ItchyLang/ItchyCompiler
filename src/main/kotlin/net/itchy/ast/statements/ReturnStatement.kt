package net.itchy.ast.statements

import net.itchy.ast.StatementVisitor
import net.itchy.ast.expressions.Expression

data class ReturnStatement(
    val returnExpression: Expression
): Statement() {
    override fun <R> visit(visitor: StatementVisitor<R>): R {
        return visitor.visit(this)
    }
}