package net.itchy.ast.statements

import net.itchy.ast.StatementVisitor
import net.itchy.ast.data.Parameter
import net.itchy.ast.data.ItchyType

data class FunctionStatement(
    val name: String,
    val returnType: ItchyType,
    val parameters: List<Parameter>,
    val statements: List<Statement>
): Statement() {
    override fun <R> visit(visitor: StatementVisitor<R>): R {
        return visitor.visit(this)
    }
}