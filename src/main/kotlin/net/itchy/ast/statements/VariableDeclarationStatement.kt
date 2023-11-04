package net.itchy.ast.statements

import net.itchy.ast.StatementVisitor
import net.itchy.ast.data.ItchyType
import net.itchy.ast.expressions.Expression

data class VariableDeclarationStatement(
    val name: String,
    val type: ItchyType,
    val expression: Expression?
): Statement() {
    init {
        this.expression?.let { this.addParentTo(it) }
    }

    override fun <R> visit(visitor: StatementVisitor<R>): R {
        return visitor.visit(this)
    }
}