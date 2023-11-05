package net.itchy.ast

import net.itchy.ast.statements.*

interface StatementVisitor<R> {
    fun visit(statement: FunctionCallStatement): R

    fun visit(statement: FunctionStatement): R

    fun visit(statement: IfStatement): R

    fun visit(statement: LoopCountStatement): R

    fun visit(statement: LoopForeverStatement): R

    fun visit(statement: LoopUntilStatement): R

    fun visit(statement: ReturnStatement): R

    fun visit(statement: SpriteStatement): R

    fun visit(statement: VariableAssignStatement): R

    fun visit(statement: VariableDeclarationStatement): R

    fun visit(statement: WhenStatement): R
}