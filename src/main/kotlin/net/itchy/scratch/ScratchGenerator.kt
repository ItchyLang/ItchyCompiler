package net.itchy.scratch

import net.itchy.ast.ExpressionVisitor
import net.itchy.ast.StatementVisitor
import net.itchy.ast.expressions.*
import net.itchy.ast.statements.*
import net.itchy.compiler.token.TokenType
import net.itchy.scratch.assets.loadCostume
import net.itchy.scratch.representation.*
import net.itchy.utils.Either
import net.itchy.utils.VariantValue
import java.util.*

class ScratchGenerator: ExpressionVisitor<Input>, StatementVisitor<Unit> {
    val scratchProject = ScratchProject()

    val stage = Stage()
    var currentSprite: Sprite? = null

    var lastBlock: Block? = null

    init {
        this.scratchProject.targets.add(this.stage)
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
                "${parameterName}1" to left,
                "${parameterName}2" to right
            )
        )
        this.addNestedBlock(block, expression.parent.id)
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
            shadowState = 1,
            actualInput = Either.right(InputSpec(4, VariantValue(expression.number), null)),
            obscuredShadow = null
        )
    }

    override fun visit(expression: StringLiteralExpression): Input {
        return Input(
            shadowState = 1,
            actualInput = Either.right(InputSpec(10, VariantValue(expression.literal), null)),
            obscuredShadow =null
        )
    }

    override fun visit(expression: UnaryOperationExpression): Input {
        TODO("Not yet implemented")
    }

    override fun visit(expression: VariableAccessExpression): Input {
        TODO("Not yet implemented")
    }

    override fun visit(statement: FunctionCallStatement) {
        val expression = statement.expression
        if (expression.name == "load_costume") {
            val name = expression.arguments[0] as StringLiteralExpression
            val location = expression.arguments[1] as StringLiteralExpression
            val costume = loadCostume(name.literal, location.literal)
            this.currentSprite?.costumes?.add(costume)
        }

        if (expression.name == "load_backdrop") {
            val name = expression.arguments[0] as StringLiteralExpression
            val location = expression.arguments[1] as StringLiteralExpression
            val costume = loadCostume(name.literal, location.literal, 480, 360)
            this.stage.costumes.add(costume)
        }

        if (expression.name == "say") {
            val block = Block(
                id = expression.id,
                opcode = "looks_say",
                topLevel = false,
                inputs = mapOf(
                    "MESSAGE" to expression.arguments[0].visit(this)
                )
            )
            this.addSerialBlock(block)
        }

        TODO("Not yet implemented")
    }

    override fun visit(statement: FunctionStatement) {
        TODO("Not yet implemented")
    }

    override fun visit(statement: IfStatement) {
        TODO("Not yet implemented")
    }

    override fun visit(statement: LoopCountStatement) {
        TODO("Not yet implemented")
    }

    override fun visit(statement: LoopForeverStatement) {
        TODO("Not yet implemented")
    }

    override fun visit(statement: LoopUntilStatement) {
        TODO("Not yet implemented")
    }

    override fun visit(statement: SpriteStatement) {
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
    }

    override fun visit(statement: VariableAssignStatement) {
        TODO("Not yet implemented")
    }

    override fun visit(statement: VariableDeclarationStatement) {

        val variable = Variable(statement.name, VariantValue(0.0))
        val variableId = UUID.randomUUID().toString()

        val current = this.currentSprite
        if (current == null) {
            // We are in the global scope (stage)
            this.stage.variables[variableId] = variable
        }

        TODO()
    }

    override fun visit(statement: WhenStatement) {
        val opcode = when (statement.event) {
            "init" -> "event_whenflagclicked"
            else -> TODO("Other when statement opcodes")
        }
        val whenBlock = Block(
            id = statement.id,
            opcode = opcode,
            parent = null,
            topLevel = true,
        )
        this.addSerialBlock(whenBlock)
        this.visitStatements(statement.statements)
    }

    private fun visitStatements(statements: List<Statement>) {
        for (sub in statements) {
            sub.visit(this)
        }
    }

    private fun addSerialBlock(block: Block) {
        val target = this.currentSprite ?: this.stage
        target.blocks[block.id] = block

        val lastBlock = this.lastBlock
        block.parent = lastBlock?.id
        if (lastBlock != null) {
            lastBlock.next = block.id
        }
        this.lastBlock = block
    }

    private fun addNestedBlock(block: Block, parent: String) {
        val target = this.currentSprite ?: this.stage
        target.blocks[block.id] = block

        block.parent = parent
    }
}
