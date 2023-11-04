package net.itchy.ast

import net.itchy.ast.statements.*

interface StatementVisitor<R> {
    fun visit(statement: ExpressionStatement): R

    fun visit(statement: FunctionStatement): R

    fun visit(statement: IfStatement): R

    fun visit(statement: LoopCountStatement): R

    fun visit(statement: LoopForeverStatement): R

    fun visit(statement: LoopUntilStatement): R

    fun visit(statement: WhenStatement): R
}