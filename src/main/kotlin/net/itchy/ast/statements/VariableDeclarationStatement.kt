package net.itchy.ast.statements

import net.itchy.ast.StatementVisitor
import net.itchy.ast.data.ItchyType
import net.itchy.ast.expressions.Expression

data class VariableDeclarationStatement(
    val name: String,
    val type: ItchyType
): Statement() {
    override fun <R> visit(visitor: StatementVisitor<R>): R {
        return visitor.visit(this)
    }
}