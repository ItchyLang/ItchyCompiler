package net.itchy.ast.statements

import net.itchy.ast.StatementVisitor

data class WhenStatement(
    val event: String,
    val eventArgument: String? = null,
    val statements: List<Statement>
): Statement() {
    init {
        this.addParentTo(this.statements)
    }

    override fun <R> visit(visitor: StatementVisitor<R>): R {
        return visitor.visit(this)
    }
}