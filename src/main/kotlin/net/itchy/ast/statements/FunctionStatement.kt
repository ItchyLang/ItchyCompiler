package net.itchy.ast.statements

import net.itchy.ast.StatementVisitor
import net.itchy.ast.data.Parameter
import net.itchy.ast.data.ItchyType

data class FunctionStatement(
    val name: String,
    val fast: Boolean,
    val returnType: ItchyType,
    val parameters: List<Parameter>,
    val statements: List<Statement>
): Statement() {
    init {
        this.addParentTo(this.statements)
    }

    override fun <R> visit(visitor: StatementVisitor<R>): R {
        return visitor.visit(this)
    }
}