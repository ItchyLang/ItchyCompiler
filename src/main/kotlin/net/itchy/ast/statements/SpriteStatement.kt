package net.itchy.ast.statements

import net.itchy.ast.StatementVisitor

data class SpriteStatement(
    val name: String,
    val statements: List<Statement>
): Statement() {
    init {
        this.addParentTo(this.statements)
    }

    override fun <R> visit(visitor: StatementVisitor<R>): R {
        return visitor.visit(this)
    }
}