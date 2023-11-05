package net.itchy.compiler.parser

import net.itchy.ast.data.ItchyType
import net.itchy.ast.data.Parameter
import net.itchy.ast.expressions.*
import net.itchy.ast.statements.*
import net.itchy.compiler.CompileException
import net.itchy.compiler.token.Token
import net.itchy.compiler.token.TokenPosition
import net.itchy.compiler.token.TokenType
import net.itchy.compiler.token.TokenType.*
import net.itchy.utils.StringUtils.toNumber

class Parser(tokens: List<Token>) {
    private val reader = TokenReader(tokens)

    fun parse(): List<Statement> {
        val statements = ArrayList<Statement>()

        while (!this.reader.isAtEnd()) {
            statements.addAll(this.globalDeclaration())
        }

        return statements
    }

    private fun globalDeclaration(): List<Statement> {
        return if (this.reader.isType(SPRITE)) listOf(this.spriteDeclaration()) else this.declaration()
    }

    private fun declaration(): List<Statement> {
        return when (this.reader.type()) {
            WHEN -> listOf(this.whenDeclaration())
            FUNC, FAST -> listOf(this.functionDeclaration())
            LET -> this.letDeclaration()
            else -> throw CompileException(this.reader.position())
        }
    }

    private fun spriteDeclaration(): SpriteStatement {
        this.reader.mustMatch(SPRITE)
        val id = this.reader.mustMatch(IDENTIFIER)
        this.reader.mustMatch(LEFT_CURLY_BRACKET)

        val spriteStatements = ArrayList<Statement>()

        while (!this.reader.isMatch(RIGHT_CURLY_BRACKET)) {
            spriteStatements.addAll(this.declaration())
        }

        return SpriteStatement(id.content, spriteStatements)
    }

    private fun whenDeclaration(): WhenStatement {
        val position = this.reader.mustMatch(WHEN).position
        val whenId = this.reader.mustMatch(IDENTIFIER)
        val whenArgument = this.reader.match(STRING)
        val statements = this.statements()
        return WhenStatement(whenId.content, whenArgument?.content, statements, position)
    }

    private fun functionDeclaration(): FunctionStatement {
        val fast = this.reader.isMatch(FAST)
        this.reader.mustMatch(FUNC)
        val id = this.reader.mustMatch(IDENTIFIER)

        val parameters = ArrayList<Parameter>()

        this.reader.mustMatch(LEFT_BRACKET)

        if (!this.reader.isMatch(RIGHT_BRACKET)) {
            parameters.add(this.createParameter())
            while (!this.reader.isMatch(RIGHT_BRACKET)) {
                this.reader.mustMatch(COMMA)
                parameters.add(this.createParameter())
            }
        }

        val type = if (this.reader.isMatch(COLON)) {
            this.identifierToType(this.reader.mustMatch(IDENTIFIER))
        } else ItchyType.VOID

        return FunctionStatement(id.content, fast, type, parameters, this.statements(), id.position)
    }

    private fun letDeclaration(): List<Statement> {
        this.reader.mustMatch(LET)
        val id = this.reader.mustMatch(IDENTIFIER)
        this.reader.mustMatch(COLON)
        val type = this.identifierToType(this.reader.mustMatch(IDENTIFIER))

        val statements = ArrayList<Statement>()
        statements.add(VariableDeclarationStatement(id.content, type))

        if (this.reader.isMatch(ASSIGN)) {
            val position = this.reader.position()
            statements.add(VariableAssignStatement(id.content, this.expression(), position))
        }

        return statements
    }

    private fun statements(): List<Statement> {
        this.reader.mustMatch(LEFT_CURLY_BRACKET)

        if (this.reader.isMatch(RIGHT_CURLY_BRACKET)) {
            return emptyList()
        }

        val statements = ArrayList<Statement>()
        statements.addAll(this.statement())
        while (!this.reader.isMatch(RIGHT_CURLY_BRACKET)) {
            this.reader.mustPreviouslyMatch(NEW_LINE)
            statements.addAll(this.statement())
        }

        return statements
    }

    private fun statement(): List<Statement> {
        return when (this.reader.type()) {
            LET -> this.letDeclaration()
            IF -> listOf(this.ifStatement())
            LOOP -> listOf(this.loopStatement())
            RETURN -> listOf(this.returnStatement())
            else -> listOf(this.expressionStatement())
        }
    }

    private fun ifStatement(): IfStatement {
        val position = this.reader.mustMatch(IF).position
        val condition = this.expression()
        val ifStatements = this.statements()

        val elseStatements = if (this.reader.isMatch(ELSE)) {
            if (this.reader.isType(IF)) {
                listOf(this.ifStatement())
            } else {
                this.statements()
            }
        } else emptyList()

        return IfStatement(condition, ifStatements, elseStatements, position)
    }

    private fun loopStatement(): Statement {
        val position = this.reader.mustMatch(LOOP).position

        return when {
            this.reader.isMatch(FOREVER) -> LoopForeverStatement(this.statements(), position)
            this.reader.isMatch(COUNT) -> LoopCountStatement(this.expression(), this.statements(), position)
            this.reader.isMatch(UNTIL) -> LoopUntilStatement(this.expression(), this.statements(), position)
            else -> throw CompileException(this.reader.position(), "Loop must be followed by 'forever', 'count', or 'until'")
        }
    }

    private fun returnStatement(): Statement {
        val position = this.reader.mustMatch(RETURN).position
        return ReturnStatement(this.expression(), position)
    }

    private fun expressionStatement(): Statement {
        val position = this.reader.position()
        val left = this.expression()

        if (this.reader.isMatch(ASSIGN)) {
            if (left !is VariableAccessExpression) {
                throw CompileException(this.reader.position(-1))
            }
            return VariableAssignStatement(left.name, this.expression(), position)
        }
        TokenType.ASSIGNMENTS_TO_OPERATOR[this.reader.type()]?.let {
            this.reader.advance()
            return this.binaryAssignment(left, it, this.expression(), position)
        }

        if (left !is FunctionCallExpression) {
            throw CompileException(position)
        }

        return FunctionCallStatement(left, position)
    }

    private fun expression(): Expression {
        return this.logicalOr()
    }

    private fun binaryAssignment(
        left: Expression,
        operator: TokenType,
        right: Expression,
        position: TokenPosition
    ): VariableAssignStatement {
        if (left !is VariableAccessExpression) {
            throw CompileException(this.reader.position(-1))
        }
        return VariableAssignStatement(left.name, BinaryOperationExpression(left, right, operator), position)
    }

    private fun logicalOr(): Expression {
        var left = this.logicalAnd()
        while (true) {
            val current = this.reader.match(OR)
            current?.let {
                val right = this.logicalAnd()
                left = BinaryOperationExpression(left, right, it.type)
            } ?: break
        }
        return left
    }

    private fun logicalAnd(): Expression {
        var left = this.equality()
        while (true) {
            val current = this.reader.match(AND)
            current?.let {
                val right = this.equality()
                left = BinaryOperationExpression(left, right, it.type)
            } ?: break
        }
        return left
    }

    private fun equality(): Expression {
        var left = this.relational()
        while (true) {
            val current = this.reader.match(EQUALS, NOT_EQUALS)
            current?.let {
                val right = this.relational()
                left = BinaryOperationExpression(left, right, it.type)
            } ?: break
        }
        return left
    }

    private fun relational(): Expression {
        var left = this.additive()
        while (true) {
            val current = this.reader.match(LT, LT_EQUALS, GT, GT_EQUALS)
            current?.let {
                val right = this.additive()
                left = BinaryOperationExpression(left, right, it.type)
            } ?: break
        }
        return left
    }

    private fun additive(): Expression {
        var left = this.multiplicative()
        while (true) {
            val current = this.reader.match(PLUS, MINUS)
            current?.let {
                val right = this.multiplicative()
                left = BinaryOperationExpression(left, right, it.type)
            } ?: break
        }
        return left
    }

    private fun multiplicative(): Expression {
        var left = unary()
        while (true) {
            val current = this.reader.match(MULTIPLY, DIVIDE, MODULUS)
            current?.let {
                val right = unary()
                left = BinaryOperationExpression(left, right, it.type)
            } ?: break
        }
        return left
    }

    private fun unary(): Expression {
        val current = this.reader.match(NOT, PLUS, MINUS)
        current?.let {
            val unary = this.unary()
            return UnaryOperationExpression(unary, it.type)
        }
        return post()
    }

    private fun post(): Expression {
        val expression = this.atom()

        if (this.reader.isMatch(LEFT_BRACKET)) {
            if (expression !is VariableAccessExpression) {
                throw CompileException(this.reader.position())
            }

            val arguments = ArrayList<Expression>()

            if (!this.reader.isMatch(RIGHT_BRACKET)) {
                arguments.add(this.expression())
                while (!this.reader.isMatch(RIGHT_BRACKET)) {
                    this.reader.mustMatch(COMMA)
                    arguments.add(this.expression())
                }
            }

            return FunctionCallExpression(expression.name, arguments, expression.position)
        }
        return expression
    }

    private fun atom(): Expression {
        val token = this.reader.peek()
        return when {
            this.reader.isMatch(TRUE) -> BooleanLiteralExpression(true)
            this.reader.isMatch(FALSE) -> BooleanLiteralExpression(false)
            this.reader.isMatch(NUMBER) -> NumberLiteralExpression(token.content.toNumber())
            this.reader.isMatch(STRING) -> StringLiteralExpression(token.content.substring(1, token.content.length - 1))
            this.reader.isMatch(IDENTIFIER) -> VariableAccessExpression(token.content, token.position)
            this.reader.isMatch(LEFT_BRACKET) -> {
                val expression = this.expression()
                this.reader.mustMatch(RIGHT_BRACKET) {
                    "Expected closing ')' after expression"
                }
                BracketExpression(expression)
            }
            else -> throw CompileException(token.position)
        }
    }

    private fun identifierToType(token: Token): ItchyType {
        return ItchyType.fromString(token.content) ?: throw CompileException(token.position)
    }

    private fun createParameter(): Parameter {
        val parameterId = this.reader.mustMatch(IDENTIFIER)
        this.reader.mustMatch(COLON)
        val type = this.identifierToType(this.reader.mustMatch(IDENTIFIER))
        return Parameter(parameterId.content, type)
    }
}