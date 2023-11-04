package net.itchy.ast.statements

import net.itchy.ast.StatementVisitor

data class LoopForeverStatement(
    val statements: List<Statement>
): Statement() {
    override fun <R> visit(visitor: StatementVisitor<R>): R {
        return visitor.visit(this)
    }
}