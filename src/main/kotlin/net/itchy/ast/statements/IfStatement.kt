package net.itchy.ast.statements

import net.itchy.ast.StatementVisitor
import net.itchy.ast.expressions.Expression

data class IfStatement(
    val condition: Expression,
    val ifStatements: List<Statement>,
    val elseStatements: List<Statement>
): Statement() {
    init {
        this.addParentTo(this.condition)
        this.addParentTo(this.ifStatements)
        this.addParentTo(this.elseStatements)
    }

    override fun <R> visit(visitor: StatementVisitor<R>): R {
        return visitor.visit(this)
    }
}