package net.itchy.ast.statements

import net.itchy.ast.StatementVisitor
import net.itchy.ast.expressions.Expression

data class VariableAssignStatement(
    val name: String,
    val assignee: Expression
): Statement() {
    init {
        this.addParentTo(this.assignee)
    }

    override fun <R> visit(visitor: StatementVisitor<R>): R {
        return visitor.visit(this)
    }
}