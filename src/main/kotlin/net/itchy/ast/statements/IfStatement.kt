package net.itchy.ast.statements

import net.itchy.ast.StatementVisitor
import net.itchy.ast.expressions.Expression

data class IfStatement(
    val condition: Expression,
    val ifStatements: List<Statement>,
    val elseStatements: List<Statement>
): Statement() {
    override fun <R> visit(visitor: StatementVisitor<R>): R {
        return visitor.visit(this)
    }
}