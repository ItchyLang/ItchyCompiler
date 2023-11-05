package net.itchy.ast.statements

import net.itchy.ast.StatementVisitor
import net.itchy.ast.expressions.Expression
import net.itchy.compiler.token.TokenPosition

data class VariableAssignStatement(
    val name: String,
    val assignee: Expression,
    val position: TokenPosition
): Statement() {
    init {
        this.addParentTo(this.assignee)
    }

    override fun <R> visit(visitor: StatementVisitor<R>): R {
        return visitor.visit(this)
    }
}