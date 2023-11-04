package net.itchy.compiler.parser

import net.itchy.ast.data.ItchyType
import net.itchy.ast.data.Parameter
import net.itchy.ast.expressions.Expression
import net.itchy.ast.statements.*
import net.itchy.compiler.CompileException
import net.itchy.compiler.token.Token
import net.itchy.compiler.token.TokenType.*

class Parser(tokens: List<Token>) {
    val reader = TokenReader(tokens)

    fun parse(): List<Statement> {
        val statements = ArrayList<Statement>()

        while (!this.reader.isAtEnd()) {
            statements.add(this.globalDeclaration())
        }

        return statements
    }

    private fun globalDeclaration(): Statement {
        return if (this.reader.isType(SPRITE)) this.spriteDeclaration() else this.declaration()
    }

    private fun declaration(): Statement {
        return when (this.reader.type()) {
            WHEN -> this.whenDeclaration()
            FUNC, FAST -> this.functionDeclaration()
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
            spriteStatements.add(this.declaration())
        }

        return SpriteStatement(id.content, spriteStatements)
    }

    private fun whenDeclaration(): WhenStatement {
        this.reader.mustMatch(WHEN)
        val whenId = this.reader.mustMatch(IDENTIFIER)
        val whenArgument = this.reader.match(STRING)
        val statements = this.statements()
        return WhenStatement(whenId.content, whenArgument?.content, statements)
    }

    private fun functionDeclaration(): FunctionStatement {
        val fast = this.reader.isMatch(FAST)
        this.reader.mustMatch(FUNC)
        val id = this.reader.mustMatch(IDENTIFIER)

        val parameters = ArrayList<Parameter>()

        this.reader.mustMatch(LEFT_BRACKET)
        while (!this.reader.isMatch(RIGHT_BRACKET)) {
            val parameterId = this.reader.mustMatch(IDENTIFIER)
            this.reader.mustMatch(COLON)
            val type = this.identifierToType(this.reader.mustMatch(IDENTIFIER))

            this.reader.isMatch(COMMA)

            parameters.add(Parameter(parameterId.content, type))
        }

        val type = if (this.reader.isMatch(COLON)) {
            this.identifierToType(this.reader.mustMatch(IDENTIFIER))
        } else ItchyType.VOID

        return FunctionStatement(id.content, fast, type, parameters, this.statements())
    }

    private fun letDeclaration(): VariableDeclarationStatement {
        this.reader.mustMatch(LET)
        val id = this.reader.mustMatch(IDENTIFIER)
        this.reader.mustMatch(COLON)
        val type = this.identifierToType(this.reader.mustMatch(IDENTIFIER))

        val expression = if (this.reader.isMatch(ASSIGN)) this.expression() else null
        return VariableDeclarationStatement(id.content, type, expression)
    }

    private fun statements(): List<Statement> {
        this.reader.mustMatch(LEFT_CURLY_BRACKET)

        if (this.reader.isMatch(RIGHT_CURLY_BRACKET)) {
            return emptyList()
        }

        val statements = ArrayList<Statement>()
        statements.add(this.statement())
        while (!this.reader.isMatch(RIGHT_CURLY_BRACKET)) {
            this.reader.mustMatch(NEW_LINE)
            statements.add(this.statement())
        }

        return statements
    }

    private fun statement(): Statement {
        return when (this.reader.type()) {
            LET -> this.letDeclaration()
            IF -> this.ifStatement()
            LOOP -> this.loopStatement()
            else -> ExpressionStatement(this.expression())
        }
    }

    private fun ifStatement(): IfStatement {
        this.reader.mustMatch(IF)
        val condition = this.expression()
        val ifStatements = this.statements()

        val elseStatements = if (this.reader.isMatch(ELSE)) {
            if (this.reader.isType(IF)) {
                listOf(this.ifStatement())
            } else {
                this.statements()
            }
        } else emptyList()

        return IfStatement(condition, ifStatements, elseStatements)
    }

    private fun loopStatement(): Statement {
        this.reader.mustMatch(LOOP)

        return when {
            this.reader.isMatch(FOREVER) -> LoopForeverStatement(this.statements())
            this.reader.isMatch(COUNT) -> LoopCountStatement(this.expression(), this.statements())
            this.reader.isMatch(UNTIL) -> LoopUntilStatement(this.expression(), this.statements())
            else -> throw CompileException(this.reader.position(), "Loop must be followed by 'forever', 'count', or 'until'")
        }
    }

    private fun expression(): Expression {
        TODO()
    }

    private fun identifierToType(token: Token): ItchyType {
        return ItchyType.fromString(token.content) ?: throw CompileException(token.position)
    }
}