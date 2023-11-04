package net.itchy.scratch

import net.itchy.ast.ExpressionVisitor
import net.itchy.ast.StatementVisitor
import net.itchy.ast.expressions.*
import net.itchy.ast.statements.*
import net.itchy.compiler.token.TokenType
import net.itchy.scratch.representation.*
import net.itchy.utils.Either
import net.itchy.utils.VariantValue
import java.util.*

class ScratchGenerator: ExpressionVisitor<Input>, StatementVisitor<Block?>
{
    val scratchProject = ScratchProject()

    val stage = Stage()
    var currentSprite: Sprite? = null

    var lastBlock: Block? = null


    init {

    }

    override fun visit(expression: BinaryOperationExpression): Input {
        val right = expression.right.visit(this)
        val left = expression.left.visit(this)
        val (opcode, parameterName) = when (expression.operator) {
            TokenType.PLUS -> "operator_add" to "NUM"
            TokenType.MINUS -> "operator_subtract" to "NUM"
            TokenType.MULTIPLY -> "operator_multiply" to "NUM"
            TokenType.DIVIDE -> "operator_divide" to "NUM"
            TokenType.MODULUS -> "operator_mod" to "NUM"
            TokenType.AND -> "operator_and" to "OPERAND"
            TokenType.OR -> "operator_or" to "OPERAND"
            TokenType.LT -> "operator_lt" to "OPERAND"
            TokenType.GT -> "operator_gt" to "OPERAND"
            TokenType.EQUALS -> "operator_equals" to "OPERAND"
            else -> TODO()
        }
        val block = Block(
            id = expression.id,
            opcode = opcode,
            parent = expression.parent.id,
            next = null,
            topLevel = false,
            inputs = mapOf(
                "${parameterName}1" to right,
                "${parameterName}2" to left
            )
        )
        this.addBlockToCurrentTarget(block)
        return Input(
            3,
            Either.left(block.id),
            Either.right(InputSpec(4, VariantValue(0.0)))
        )
    }

    override fun visit(expression: BooleanLiteralExpression): Input {
        TODO("Not yet implemented")
    }

    override fun visit(expression: BracketExpression): Input {
        TODO("Not yet implemented")
    }

    override fun visit(expression: FunctionCallExpression): Input {
        TODO("Not yet implemented")
    }

    override fun visit(expression: NumberLiteralExpression): Input {
        return Input(
            1,
            Either.right(InputSpec(4, VariantValue(expression.number), null)),
            null
        )
    }

    override fun visit(expression: StringLiteralExpression): Input {
        TODO("Not yet implemented")
    }

    override fun visit(expression: UnaryOperationExpression): Input {
        TODO("Not yet implemented")
    }

    override fun visit(expression: VariableAccessExpression): Input {
        TODO("Not yet implemented")
    }

    override fun visit(statement: FunctionCallStatement): Block {
        val expression = statement.expression
        if (expression.name == "say") {
            // Block(
            //     opcode = "looks_say",
            //     next =
            // )
        }

        TODO("Not yet implemented")
    }

    override fun visit(statement: FunctionStatement): Block {
        TODO("Not yet implemented")
    }

    override fun visit(statement: IfStatement): Block {
        TODO("Not yet implemented")
    }

    override fun visit(statement: LoopCountStatement): Block {
        TODO("Not yet implemented")
    }

    override fun visit(statement: LoopForeverStatement): Block {
        TODO("Not yet implemented")
    }

    override fun visit(statement: LoopUntilStatement): Block {
        TODO("Not yet implemented")
    }

    override fun visit(statement: SpriteStatement): Block? {
        val sprite = Sprite(
            name = statement.name,
        )
        this.scratchProject.targets.add(sprite)
        this.currentSprite = sprite

        for (child in statement.statements) {
            child.visit(this)
        }

        this.currentSprite = null
        // THIS IS JANK!!!
        return null
    }

    override fun visit(statement: VariableAssignStatement): Block {

        TODO("Not yet implemented")
    }

    override fun visit(statement: VariableDeclarationStatement): Block? {

        val variable = Variable(statement.name, VariantValue(0.0))
        val variableId = UUID.randomUUID().toString()

        val current = this.currentSprite
        if (current == null) {
            // We are in the global scope (stage)
            this.stage.variables[variableId] = variable
        }

        TODO()
    }

    override fun visit(whenStatement: WhenStatement): Block? {
        if (whenStatement.statements.isEmpty()) {
            TODO()
        }

        val first = whenStatement.statements[0]
        if (whenStatement.event == "init") {
            val whenBlock = Block(
                id = whenStatement.id,
                opcode = "event_whenflagclicked",
                next = first.id,
                parent = null,
                topLevel = true,
            )
            this.addBlockToCurrentTarget(whenBlock)

            for (sub in whenStatement.statements) {
                sub.visit(this)
            }
        }
        TODO()
    }

    private fun visitStatements(statements: List<Statement>) {

    }

    private fun addBlockToCurrentTarget(block: Block) {
        val target = this.currentSprite ?: this.stage
        target.blocks[block.id] = block

        val lastBlock = this.lastBlock
        block.parent = lastBlock?.id
        if (lastBlock != null) {
            lastBlock.next = block.id
        }
        this.lastBlock = block
    }
}
