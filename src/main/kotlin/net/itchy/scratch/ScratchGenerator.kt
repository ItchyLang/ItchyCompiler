package net.itchy.scratch

import net.itchy.ast.ExpressionVisitor
import net.itchy.ast.StatementVisitor
import net.itchy.ast.data.ItchyType
import net.itchy.ast.expressions.*
import net.itchy.ast.statements.*
import net.itchy.compiler.CompileException
import net.itchy.compiler.token.TokenPosition
import net.itchy.compiler.token.TokenType
import net.itchy.scratch.assets.loadCostume
import net.itchy.scratch.representation.*
import net.itchy.utils.Either
import net.itchy.utils.VariantValue
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ScratchGenerator: ExpressionVisitor<Input>, StatementVisitor<Unit> {
    private val scratchProject = ScratchProject()

    private val stage = Stage()

    private var currentSprite: Sprite? = null

    private var currentScopes = ArrayDeque<HashMap<String, String>>()

    private var functionParameterScope: HashMap<String, Pair<String, Boolean>>? = null
    private var functionScope: HashMap<String, List<Pair<String, Boolean>>> = HashMap()

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
            TokenType.NOT_EQUALS -> {
                val unary = UnaryOperationExpression(BinaryOperationExpression(
                    expression.left, expression.right, TokenType.EQUALS
                ), TokenType.NOT)
                unary.parent = expression.parent
                return unary.visit(this)
            }
            TokenType.LT_EQUALS -> {
                val binary = BinaryOperationExpression(
                    BinaryOperationExpression(
                        expression.left, expression.right, TokenType.LT
                    ),
                    BinaryOperationExpression(
                        expression.left, expression.right, TokenType.EQUALS
                    ),
                    TokenType.OR
                )
                binary.parent = expression.parent
                return binary.visit(this)
            }
            TokenType.GT_EQUALS -> {
                val binary = BinaryOperationExpression(
                    BinaryOperationExpression(
                        expression.left, expression.right, TokenType.GT
                    ),
                    BinaryOperationExpression(
                        expression.left, expression.right, TokenType.EQUALS
                    ),
                    TokenType.OR
                )
                binary.parent = expression.parent
                return binary.visit(this)
            }
            else -> throw IllegalArgumentException()
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
        val fScope = this.functionScope
        val data = fScope[expression.name]
        if (data != null) {
            if (data.size != expression.arguments.size) {
                throw CompileException(expression.position, "Argument sizes do not match")
            }

            val inputs = HashMap<String, Input>()
            for ((i, pair) in data.withIndex()) {
                inputs[pair.first] = expression.arguments[i].visit(this)
                    .copy(shadowState = if (pair.second) 2 else 1)
            }

            val callBlock = Block(
                id = expression.id,
                opcode = "procedures_call",
                inputs = inputs,
                topLevel = false,
                mutation = ProcedureMutation(
                    procCode = "${expression.name} ${data.joinToString(" ") { if (it.second) "%b" else "%s" }}",
                    argumentIds = data.map { it.first },
                    warp = false,
                    argumentNames = emptyList()
                )
            )
            this.addSerialBlock(callBlock, expression.position)
        } else {
            return Input(
                shadowState = 1,
                actualInput = Either.right(InputSpec(4, VariantValue(0.0), null)),
                obscuredShadow = null
            )
            TODO("Not yet implemented")
        }

        val lengthOflist = Block(
            id = UUID.randomUUID().toString(),
            opcode = "data_lengthoflength",
            topLevel = false,
            fields = hashMapOf(
                "LIST" to Field(VariantValue("returns"), "wellsmuir")
            )
        )
        val dataInList = Block(
            id = UUID.randomUUID().toString(),
            opcode = "data_itemoflist",
            topLevel = false,
            inputs = mapOf(
                "INDEX" to Input(
                    3,
                    Either.left(lengthOflist.id),
                    Either.right(InputSpec())
                )
            ),
            fields = hashMapOf(
                "LIST" to Field(VariantValue("returns"), "wellsmuir")
            )
        )
        lengthOflist.parent = dataInList.id
        dataInList.parent = expression.parent.id

        return Input(
            shadowState = 1,
            actualInput = Either.left(dataInList.id),
            obscuredShadow = Either.right(InputSpec())
        )
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
        if (expression.name == "x_position") {
            val xPositionBlock = Block(
                id = UUID.randomUUID().toString(),
                opcode = "motion_xposition",
                topLevel = false
            )
            this.addNestedBlock(xPositionBlock, expression.parent.id)
            return Input(
                shadowState = 3,
                actualInput = Either.left(xPositionBlock.id),
                obscuredShadow = Either.right(InputSpec())
            )
        }
        if (expression.name == "y_position") {
            val yPositionBlock = Block(
                id = UUID.randomUUID().toString(),
                opcode = "motion_yposition",
                topLevel = false
            )
            this.addNestedBlock(yPositionBlock, expression.parent.id)
            return Input(
                shadowState = 3,
                actualInput = Either.left(yPositionBlock.id),
                obscuredShadow = Either.right(InputSpec())
            )
        }

        val scope = this.functionParameterScope
        if (scope != null) {
            val data = scope[expression.name]
            if (data != null) {
                val (id, isBoolean) = data
                val reporterBlock = Block(
                    id = UUID.randomUUID().toString(),
                    opcode = "argument_reporter_" + if (isBoolean) "boolean" else "string_number",
                    fields = hashMapOf(
                        "VALUE" to Field(VariantValue(expression.name), null)
                    ),
                    topLevel = false
                )
                this.addNestedBlock(reporterBlock, expression.parent.id)
                return Input(
                    shadowState = 3,
                    actualInput = Either.left(reporterBlock.id),
                    obscuredShadow = Either.right(InputSpec())
                )
            }
        }

        val (id, localName) = getVariableId(expression.name, expression.position)

        return Input(
            shadowState = 3,
            actualInput = Either.right(InputSpec(12, VariantValue(localName), id)),
            obscuredShadow = Either.right(InputSpec())
        )
    }

    override fun visit(statement: FunctionCallStatement) {
        val expression = statement.expression
        if (expression.name == "load_costume") {
            val name = expression.arguments[0] as StringLiteralExpression
            val location = expression.arguments[1] as StringLiteralExpression
            val costume = loadCostume(name.literal, location.literal)
            this.currentSprite?.costumes?.add(costume)
            return
        }
        if (expression.name == "load_backdrop") {
            val name = expression.arguments[0] as StringLiteralExpression
            val location = expression.arguments[1] as StringLiteralExpression
            val costume = loadCostume(name.literal, location.literal, 480, 360)
            this.stage.costumes.add(costume)
            return
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
            this.addSerialBlock(block, statement.position)
            return
        }
        if (expression.name == "broadcast") {
            val broadcastID = UUID.randomUUID().toString()
            val broadcastName = (expression.arguments[0] as StringLiteralExpression).literal

            // Add broadcast to stages broadcasts map
            stage.broadcasts[broadcastID] = broadcastName

            val broadcastBlock = Block(
                id = statement.id,
                opcode = "event_broadcast",
                topLevel = false,
                inputs = hashMapOf("BROADCAST_INPUT" to Input(
                    shadowState = 1,
                    actualInput = Either.right(InputSpec(11, VariantValue(broadcastName), broadcastID)),
                    obscuredShadow = null
                ))
            )

            this.addSerialBlock(broadcastBlock, statement.position)
            return
        }
        if (expression.name == "go_to")
        {
            var block = Block(
                id = statement.id,
                opcode = "motion_gotoxy",
                topLevel = false,
                inputs = hashMapOf("X" to expression.arguments[0].visit(this),
                    "Y" to expression.arguments[1].visit((this)))
            )
            this.addSerialBlock(block, statement.position)
            return
        }


        expression.visit(this)
    }

    override fun visit(statement: FunctionStatement) {
        val inputs = HashMap<String, Input>()
        val argumentIds = ArrayList<String>()
        // Construct Function Prototype
        val funcProtoBlock = Block(
            id = UUID.randomUUID().toString(),
            opcode = "procedures_prototype",
            topLevel = false,
            inputs = inputs,
            shadow = true,
            mutation = ProcedureMutation(
                procCode = "${statement.name} ${statement.parameters.joinToString(" ") { if (it.type == ItchyType.BOOLEAN) "%b" else "%s" }}",
                argumentIds = argumentIds,
                argumentNames = statement.parameters.map { it.name },
                warp = statement.fast
            )
        )

        val scope = HashMap<String, Pair<String, Boolean>>()
        this.functionParameterScope = scope

        val paramData = ArrayList<Pair<String, Boolean>>()
        this.functionScope[statement.name] = paramData

        for (parameter in statement.parameters) {
            val paramBlock = Block(
                id = UUID.randomUUID().toString(),
                opcode = "argument_reporter_" + if (parameter.type == ItchyType.BOOLEAN) "boolean" else "string_number",
                fields = hashMapOf(
                    "VALUE" to Field(VariantValue(parameter.name), null)
                ),
                shadow = true,
                topLevel = false
            )
            val newId = UUID.randomUUID().toString()
            argumentIds.add(newId)
            inputs[newId] = Input(1, Either.left(paramBlock.id), null)
            this.addNestedBlock(paramBlock, funcProtoBlock.id)
            scope[parameter.name] = paramBlock.id to (parameter.type == ItchyType.BOOLEAN)
            paramData.add(newId to (parameter.type == ItchyType.BOOLEAN))
        }

        this.addNestedBlock(funcProtoBlock, statement.id)

        // Construct Function Definition
        val funcDefBlock = Block(
            id = statement.id,
            opcode = "procedures_definition",
            topLevel = true,
            inputs = mapOf(
                "custom_block" to Input(
                    shadowState = 1,
                    actualInput = Either.left(funcProtoBlock.id),
                    obscuredShadow = null
                )
            )
        )
        this.lastBlock = null
        this.addSerialBlock(funcDefBlock, statement.position)

        this.visitStatements(statement.statements)
        this.functionParameterScope = null
    }

    override fun visit(statement: IfStatement) {
        val subStack1 = if (willGenerateBlocks(statement.ifStatements)) {
            Input(
                shadowState = 2,
                actualInput = Either.left(findFirstBlock(statement.ifStatements)),
                obscuredShadow = null
            )
        } else {
            Input(
                shadowState = 1,
                actualInput = null,
                obscuredShadow = null
            )
        }
        val subStack2 = if (willGenerateBlocks(statement.elseStatements)) {
            Input(
                shadowState = 2,
                actualInput = Either.left(findFirstBlock(statement.elseStatements)),
                obscuredShadow = null
            )
        } else {
            Input(
                shadowState = 1,
                actualInput = null,
                obscuredShadow = null
            )
        }

        val previous = statement.condition.parent
        val condition = BinaryOperationExpression(
            statement.condition,
            BooleanLiteralExpression(true),
            TokenType.EQUALS
        )
        condition.parent = previous

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
        this.addSerialBlock(ifBlock, statement.position)

        this.visitStatements(statement.ifStatements)
        this.lastBlock = ifBlock
        this.visitStatements(statement.elseStatements)
        this.lastBlock = ifBlock
        ifBlock.next = null
    }

    override fun visit(statement: LoopCountStatement) {
        // Generate inputs for count and (possibly) substack
        val inputs = HashMap<String, Input>()
        inputs["TIMES"] = statement.count.visit(this)
        if (willGenerateBlocks(statement.statements))
        {
            inputs["SUBSTACK"] = Input(
                shadowState = 2,
                actualInput = Either.left(findFirstBlock(statement.statements)),
                obscuredShadow = null
            )
        }

        // Construct block
        val loopBlock = Block(
            id = statement.id,
            opcode = "control_repeat",
            topLevel = false,
            inputs = inputs
        )

        // Add block to representation and update references
        this.addSerialBlock(loopBlock, statement.position)

        this.visitStatements(statement.statements)
        this.lastBlock = loopBlock
        loopBlock.next = null
    }

    override fun visit(statement: LoopForeverStatement) {
        val inputs = if(!willGenerateBlocks(statement.statements))
        {
            hashMapOf()
        }
        else
        {
            hashMapOf("SUBSTACK" to Input(
                shadowState = 2,
                actualInput = Either.left(findFirstBlock(statement.statements)),
                obscuredShadow = null
            ))
        }

        // Construct block
        val loopBlock = Block(
            id = statement.id,
            opcode = "control_forever",
            topLevel = false,
            inputs = inputs
        )

        // Add block to representation and update references
        this.addSerialBlock(loopBlock, statement.position)

        this.visitStatements(statement.statements)
        this.hasScopeEnded = true
        this.lastBlock = null
        loopBlock.next = null
    }

    override fun visit(statement: LoopUntilStatement) {
        // THIS IS JANK!!!
        val previous = statement.condition.parent
        val condition = BinaryOperationExpression(
            statement.condition,
            BooleanLiteralExpression(true),
            TokenType.EQUALS
        )
        condition.parent = previous

        val inputs = HashMap<String, Input>()
        inputs["CONDITION"] = condition.visit(this)
        if (willGenerateBlocks(statement.statements))
        {
            inputs["SUBSTACK"] = Input(
                shadowState = 2,
                actualInput = Either.left(findFirstBlock(statement.statements)),
                obscuredShadow = null
            )
        }

        // Construct block
        val loopBlock = Block(
            id = statement.id,
            opcode = "control_repeat_until",
            topLevel = false,
            inputs = inputs
        )

        // Add block to representation and update references
        this.addSerialBlock(loopBlock, statement.position)

        this.visitStatements(statement.statements)
        this.lastBlock = loopBlock
        loopBlock.next = null
    }

    override fun visit(statement: ReturnStatement) {
        val appendBlock = Block(
            id = statement.id,
            opcode = "data_addtolist",
            inputs = mapOf(
                "ITEM" to statement.returnExpression.visit(this)
                    .copy(shadowState = 1, obscuredShadow = Either.right(InputSpec()))
            ),
            fields = hashMapOf(
                "LIST" to Field(VariantValue("returns"), "wellsmuir")
            ),
            topLevel = false
        )
        this.addSerialBlock(appendBlock, statement.position)

        val stopBlock = Block(
            id = UUID.randomUUID().toString(),
            opcode = "control_stop",
            topLevel = false,
            fields = hashMapOf(
                "STOP_OPTION" to Field(VariantValue("this script"), null)
            ),
            mutation = StopMutation(false)
        )
        this.addSerialBlock(stopBlock, statement.position)
        this.hasScopeEnded = true
        this.lastBlock = null
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
        if (this.lastBlock == null)
        {
            // Create a when init block
            val whenStatement = WhenStatement(
                "init", null, listOf(statement), statement.position
            )
            statement.parent = whenStatement
            whenStatement.visit(this)
            this.lastBlock = null
            return
        }

        // Look for variable id
        val (id, localName) = getVariableId(statement.name, statement.position)

        // Generate variable assign block
        val varAssignBlock = Block(
            id = statement.id,
            opcode = "data_setvariableto",
            topLevel = false,
            fields = hashMapOf("VARIABLE" to Field(VariantValue(localName), id)),
            inputs = hashMapOf("VALUE" to statement.assignee.visit(this)),
        )

        addSerialBlock(varAssignBlock, statement.position)
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
            "received" -> "event_whenbroadcastreceived"
            else -> TODO("Other when statement opcodes")
        }
        val whenBlock = Block(
            id = statement.id,
            opcode = opcode,
            parent = null,
            topLevel = true,
        )

        // Add broadcast field
        if (statement.event == "received")
        {
            statement.eventArgument?:throw CompileException(statement.position, "When received must specify a broadcast")
            val broadcastName = statement.eventArgument.removePrefix("\"").removeSuffix("\"")
            whenBlock.fields["BROADCAST_OPTION"] = Field(
                value = VariantValue(broadcastName),
                id = getBroadcastId(broadcastName, statement.position)
            )
        }

        this.addSerialBlock(whenBlock, statement.position)
        this.visitStatements(statement.statements)
    }

    // Called anytime we enter a new scope (if, loop, function, etc.)
    private fun visitStatements(statements: List<Statement>) {
        currentScopes.push(hashMapOf()) // Push stack frame
        for (sub in statements) {
            sub.visit(this)
        }
        this.hasScopeEnded = false
        currentScopes.pop() // Pop stack frame
    }

    private fun addSerialBlock(block: Block, position: TokenPosition) {
        if (this.hasScopeEnded) {
            throw CompileException(position, "Cannot add statements after 'loop forever' or 'stop'")
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

    private fun getVariableId(varName : String, position: TokenPosition): Pair<String, String> {
        var id : String? = null
        var localName = ""
        for (stackFrame in currentScopes.descendingIterator())
        {
            if (stackFrame.containsKey(varName))
            {
                id?.let { throw CompileException(position, "Variable $varName already defined") }
                id = stackFrame[varName]
                localName = "${varName}${System.identityHashCode(stackFrame)}"
            }
        }
        id?: throw CompileException(position, "No variable with name $varName declared")
        return id to localName
    }

    private fun willGenerateBlocks(statements: List<Statement>): Boolean {
        for (statement in statements) {
            if (statement !is VariableDeclarationStatement) {
                return true
            }
        }
        return false
    }

    private fun findFirstBlock(statements: List<Statement>): String {
        for (statement in statements) {
            if (statement !is VariableDeclarationStatement) {
                return statement.id
            }
        }
        throw IllegalStateException()
    }

    private fun getBroadcastId(desiredName : String, position: TokenPosition) : String
    {
        for ((id, name) in stage.broadcasts)
        {
            if (name == desiredName) return id
        }
        throw CompileException(position, "Undefined broadcast")
    }
}
