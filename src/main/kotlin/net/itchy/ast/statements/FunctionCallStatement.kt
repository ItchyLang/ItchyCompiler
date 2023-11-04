package net.itchy.ast.statements

import net.itchy.ast.StatementVisitor
import net.itchy.ast.expressions.Expression
import net.itchy.ast.expressions.FunctionCallExpression

data class FunctionCallStatement(
    val expression: FunctionCallExpression
): Statement() {
    init {
        this.addParentTo(this.expression)
    }

    override fun <R> visit(visitor: StatementVisitor<R>): R {
        return visitor.visit(this)
    }
}