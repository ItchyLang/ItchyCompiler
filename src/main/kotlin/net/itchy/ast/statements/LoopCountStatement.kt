package net.itchy.ast.statements

import net.itchy.ast.StatementVisitor
import net.itchy.ast.expressions.Expression

class LoopCountStatement(
    val count: Expression,
    val statements: List<Statement>
): Statement() {
    init {
        this.addParentTo(this.count)
        this.addParentTo(this.statements)
    }

    override fun <R> visit(visitor: StatementVisitor<R>): R {
        return visitor.visit(this)
    }
}