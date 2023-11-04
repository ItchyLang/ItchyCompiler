package net.itchy.ast

import net.itchy.ast.expressions.*

interface ExpressionVisitor<R> {
    fun visit(expression: BinaryOperationExpression): R

    fun visit(expression: BooleanLiteralExpression): R

    fun visit(expression: BracketExpression): R

    fun visit(expression: FunctionCallExpression): R

    fun visit(expression: NumberLiteralExpression): R

    fun visit(expression: StringLiteralExpression): R

    fun visit(expression: UnaryOperationExpression): R

    fun visit(expression: VariableAccessExpression): R

    fun visit(expression: VariableAssignExpression): R
}