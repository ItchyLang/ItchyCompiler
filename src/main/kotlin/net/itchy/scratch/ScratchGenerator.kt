package net.itchy.scratch

import net.itchy.ast.ExpressionVisitor
import net.itchy.ast.StatementVisitor
import net.itchy.ast.expressions.*
import net.itchy.ast.statements.*
import net.itchy.compiler.CompileException
import net.itchy.compiler.token.TokenType
import net.itchy.scratch.assets.loadCostume
import net.itchy.scratch.representation.*
import net.itchy.utils.Either
import net.itchy.utils.VariantValue
import java.util.*
import kotlin.collections.HashMap

class ScratchGenerator: ExpressionVisitor<Input>, StatementVisitor<Unit> {
    private val scratchProject = ScratchProject()

    private val stage = Stage()
    private var currentSprite: Sprite? = null

    private var currentScopes = ArrayDeque<HashMap<String, String>>()

    private var lastBlock: Block? = null

    private var hasScopeEnded = false

    init {
        this.scratchProject.targets.add(this.stage)
    }

    fun generate(statements: List<Statement>): ScratchProject {
        this.visitStatements(statements)
        return this.scratchProject
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
            shadowState = 3,
            actualInput = Either.left(block.id),
            obscuredShadow = Either.right(InputSpec(4, VariantValue(0.0)))
        )
    }

    override fun visit(expression: BooleanLiteralExpression): Input {
        val binary = BinaryOperationExpression(
            NumberLiteralExpression(0.0),
            NumberLiteralExpression(if (expression.literal) 0.0 else 1.0),
            TokenType.EQUALS
        )
        binary.parent = expression.parent
        return binary.visit(this)
    }

    override fun visit(expression: BracketExpression): Input {
        return expression.expression.visit(this)
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
            obscuredShadow = null
        )
    }

    override fun visit(expression: UnaryOperationExpression): Input {
        when (expression.operator) {
            TokenType.PLUS -> return expression.expression.visit(this)
            TokenType.MINUS -> {
                val binary = BinaryOperationExpression(
                    NumberLiteralExpression(0.0),
                    expression.expression,
                    TokenType.MINUS
                )
                binary.parent = expression.parent
                return binary.visit(this)
            }
            else -> { }
        }

        val block = Block(
            id = expression.id,
            opcode = "operator_not",
            topLevel = false,
            inputs = mapOf(
                "OPERAND" to expression.expression.visit(this)
            )
        )
        this.addNestedBlock(block, expression.parent.id)
        return Input(
            shadowState = 3,
            actualInput = Either.left(block.id),
            obscuredShadow = Either.right(InputSpec(4, VariantValue(0.0)))
        )
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
                id = statement.id,
                opcode = "looks_say",
                topLevel = false,
                inputs = mapOf(
                    "MESSAGE" to expression.arguments[0].visit(this)
                )
            )
            this.addSerialBlock(block)
        }
    }

    override fun visit(statement: FunctionStatement) {
        TODO("Not yet implemented")
    }

    override fun visit(statement: IfStatement) {
        val subStack1 = if (statement.ifStatements.isNotEmpty()) {
            Input(
                shadowState = 2,
                actualInput = Either.left(statement.ifStatements.first().id),
                obscuredShadow = null
            )
        } else {
            Input(
                shadowState = 1,
                actualInput = null,
                obscuredShadow = null
            )
        }
        val subStack2 = if (statement.elseStatements.isNotEmpty()) {
            Input(
                shadowState = 2,
                actualInput = Either.left(statement.elseStatements.first().id),
                obscuredShadow = null
            )
        } else {
            Input(
                shadowState = 1,
                actualInput = null,
                obscuredShadow = null
            )
        }

        val condition = BinaryOperationExpression(
            statement.condition,
            BooleanLiteralExpression(true),
            TokenType.EQUALS
        )
        condition.parent = statement.condition.parent

        val ifBlock = Block(
            id = statement.id,
            topLevel = false,
            opcode = "control_if_else",
            inputs = mapOf(
                "CONDITION" to condition.visit(this).copy(shadowState = 2, obscuredShadow = null),
                "SUBSTACK" to subStack1,
                "SUBSTACK2" to subStack2
            )
        )
        this.addSerialBlock(ifBlock)

        this.visitStatements(statement.ifStatements)
        this.lastBlock = ifBlock
        this.visitStatements(statement.elseStatements)
        this.lastBlock = ifBlock
        ifBlock.next = null
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

        this.visitStatements(statement.statements)

        this.currentSprite = null
    }

    override fun visit(statement: VariableAssignStatement) {
        TODO("Not yet implemented")
    }

    override fun visit(statement: VariableDeclarationStatement) {
        // Get local name
        val localName = "${statement.name}${System.identityHashCode(currentScopes.peek())}"

        // Construct variable
        val variable = Variable(localName, VariantValue(0.0))
        val variableId = UUID.randomUUID().toString()

        // Add to representation
        val current = this.currentSprite ?: this.stage
        current.variables[variableId] = variable

        // Add to scope
        currentScopes.peek()[statement.name] = variableId
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
        if (this.hasScopeEnded) {
            TODO("Scope has finished, please don't add more")
        }

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
