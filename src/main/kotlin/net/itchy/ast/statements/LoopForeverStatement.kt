package net.itchy.ast.statements

import net.itchy.ast.StatementVisitor
import net.itchy.compiler.token.TokenPosition

data class LoopForeverStatement(
    val statements: List<Statement>,
    val position: TokenPosition
): Statement() {
    init {
        this.addParentTo(this.statements)
    }

    override fun <R> visit(visitor: StatementVisitor<R>): R {
        return visitor.visit(this)
    }
}