package net.itchy.ast.statements

import net.itchy.ast.StatementVisitor
import net.itchy.ast.expressions.Expression
import net.itchy.compiler.token.TokenPosition

data class LoopUntilStatement(
    val condition: Expression,
    val statements: List<Statement>,
    val position: TokenPosition
): Statement() {
    init {
        this.addParentTo(this.condition)
        this.addParentTo(this.statements)
    }

    override fun <R> visit(visitor: StatementVisitor<R>): R {
        return visitor.visit(this)
    }
}