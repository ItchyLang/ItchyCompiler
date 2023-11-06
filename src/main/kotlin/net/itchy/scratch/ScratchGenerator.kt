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
    // TODO: make a proper fix for this
    private var lastBlockMod: ((String) -> Unit)? = null

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
            topLevel = false,
            inputs = mapOf(
                "${parameterName}1" to left,
                "${parameterName}2" to right
            )
        )
        this.addNestedBlock(block, expression.parent.id)
        return Input.block(block.id)
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
                var input = expression.arguments[i].visit(this)
                if (pair.second) {
                    input = input.withoutShadow()
                }
                inputs[pair.first] = input
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
            // TODO: Handle built-in functions
            println("Function did not exist... Suppressing error...")
            return Input.number(0.0)
        }

        val lengthOfList = Block(
            id = UUID.randomUUID().toString(),
            opcode = "data_lengthoflist",
            topLevel = false,
            fields = hashMapOf(
                "LIST" to Field(Either("returns"), "wellsmuir")
            )
        )
        val dataInList = Block(
            id = UUID.randomUUID().toString(),
            opcode = "data_itemoflist",
            topLevel = false,
            inputs = mapOf(
                "INDEX" to Input.block(lengthOfList.id)
            ),
            fields = hashMapOf(
                "LIST" to Field(Either("returns"), "wellsmuir")
            )
        )
        this.addNestedBlock(lengthOfList, dataInList.id)
        this.addNestedBlock(dataInList, expression.parent.id)

        return Input.block(dataInList.id)
    }

    override fun visit(expression: NumberLiteralExpression): Input {
        return Input.number(expression.number)
    }

    override fun visit(expression: StringLiteralExpression): Input {
        return Input.string(expression.literal)
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

        val previous = expression.expression.parent
        val condition = BinaryOperationExpression(
            expression.expression,
            BooleanLiteralExpression(false),
            TokenType.EQUALS
        )
        condition.parent = previous
        return condition.visit(this)
    }

    override fun visit(expression: VariableAccessExpression): Input {
        if (expression.name == "x_position") {
            val xPositionBlock = Block(
                id = UUID.randomUUID().toString(),
                opcode = "motion_xposition",
                topLevel = false
            )
            this.addNestedBlock(xPositionBlock, expression.parent.id)
            return Input.block(xPositionBlock.id)
        }
        if (expression.name == "y_position") {
            val yPositionBlock = Block(
                id = UUID.randomUUID().toString(),
                opcode = "motion_yposition",
                topLevel = false
            )
            this.addNestedBlock(yPositionBlock, expression.parent.id)
            return Input.block(yPositionBlock.id)
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
                        "VALUE" to Field(Either(expression.name), null)
                    ),
                    topLevel = false
                )
                this.addNestedBlock(reporterBlock, expression.parent.id)
                return Input.block(reporterBlock.id)
            }
        }

        val (id, localName) = getVariableId(expression.name, expression.position)

        return Input.variable(localName, id)
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
                inputs = hashMapOf("BROADCAST_INPUT" to Input.broadcast(broadcastName, broadcastID))
            )

            this.addSerialBlock(broadcastBlock, statement.position)
            return
        }
        if (expression.name == "go_to")
        {
            val block = Block(
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
                    "VALUE" to Field(Either(parameter.name), null)
                ),
                shadow = true,
                topLevel = false
            )
            val newId = UUID.randomUUID().toString()
            argumentIds.add(newId)
            inputs[newId] = Input.substack(paramBlock.id)
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
                "custom_block" to Input.substack(funcProtoBlock.id)
            )
        )
        this.lastBlock = null
        this.addSerialBlock(funcDefBlock, statement.position)

        this.visitStatements(statement.statements)
        this.functionParameterScope = null
    }

    override fun visit(statement: IfStatement) {
        val subStack1 = if (willGenerateBlocks(statement.ifStatements)) {
            // TODO: Use substack?
            Input.block(findFirstBlock(statement.ifStatements))
        } else {
            Input.nothing()
        }
        val subStack2 = if (willGenerateBlocks(statement.elseStatements)) {
            Input.block(findFirstBlock(statement.elseStatements))
        } else {
            Input.nothing()
        }

        val previous = statement.condition.parent
        val condition = BinaryOperationExpression(
            statement.condition,
            BooleanLiteralExpression(true),
            TokenType.EQUALS
        )
        condition.parent = previous

        val inputs = hashMapOf(
            "CONDITION" to condition.visit(this).withoutShadow(),
            "SUBSTACK" to subStack1,
            "SUBSTACK2" to subStack2
        )

        val ifBlock = Block(
            id = statement.id,
            topLevel = false,
            opcode = "control_if_else",
            inputs = inputs
        )
        this.addSerialBlock(ifBlock, statement.position)

        this.lastBlockMod = {
            inputs["SUBSTACK"] = Input.block(it)
        }
        this.visitStatements(statement.ifStatements)
        this.lastBlock = ifBlock

        this.lastBlockMod = {
            inputs["SUBSTACK2"] = Input.block(it)
        }
        this.visitStatements(statement.elseStatements)
        this.lastBlockMod = null
        this.lastBlock = ifBlock
        ifBlock.next = null
    }

    override fun visit(statement: LoopCountStatement) {
        // Generate inputs for count and (possibly) substack
        val inputs = HashMap<String, Input>()
        inputs["TIMES"] = statement.count.visit(this)
        if (willGenerateBlocks(statement.statements))
        {
            inputs["SUBSTACK"] = Input.block(findFirstBlock(statement.statements))
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

        this.lastBlockMod = {
            inputs["SUBSTACK"] = Input.block(it)
        }
        this.visitStatements(statement.statements)
        this.lastBlock = loopBlock
        this.lastBlockMod = null
        loopBlock.next = null
    }

    override fun visit(statement: LoopForeverStatement) {
        val inputs = if(!willGenerateBlocks(statement.statements))
        {
            hashMapOf()
        }
        else
        {
            hashMapOf("SUBSTACK" to Input.block(findFirstBlock(statement.statements)))
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

        this.lastBlockMod = {
            inputs["SUBSTACK"] = Input.block(it)
        }
        this.visitStatements(statement.statements)
        this.hasScopeEnded = true
        this.lastBlock = null
        this.lastBlockMod = null
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
            inputs["SUBSTACK"] = Input.block(findFirstBlock(statement.statements))
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

        this.lastBlockMod = {
            inputs["SUBSTACK"] = Input.block(it)
        }
        this.visitStatements(statement.statements)
        this.lastBlock = loopBlock
        this.lastBlockMod = null
        loopBlock.next = null
    }

    override fun visit(statement: ReturnStatement) {
        val appendBlock = Block(
            id = statement.id,
            opcode = "data_addtolist",
            inputs = mapOf(
                "ITEM" to statement.returnExpression.visit(this)
            ),
            fields = hashMapOf(
                "LIST" to Field(Either("returns"), "wellsmuir")
            ),
            topLevel = false
        )
        this.addSerialBlock(appendBlock, statement.position)

        val stopBlock = Block(
            id = UUID.randomUUID().toString(),
            opcode = "control_stop",
            topLevel = false,
            fields = hashMapOf(
                "STOP_OPTION" to Field(Either("this script"), null)
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
            fields = hashMapOf("VARIABLE" to Field(Either(localName), id)),
            inputs = hashMapOf("VALUE" to statement.assignee.visit(this)),
        )

        addSerialBlock(varAssignBlock, statement.position)
    }

    override fun visit(statement: VariableDeclarationStatement) {
        // Get local name
        val localName = "${statement.name}${System.identityHashCode(currentScopes.peek())}"

        // Construct variable
        val variable = Variable(localName, Either(0.0))
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
        val fields = HashMap<String, Field>()
        val whenBlock = Block(
            id = statement.id,
            opcode = opcode,
            fields = fields,
            topLevel = true,
        )

        // Add broadcast field
        if (statement.event == "received")
        {
            statement.eventArgument?:throw CompileException(statement.position, "When received must specify a broadcast")
            val broadcastName = statement.eventArgument.removePrefix("\"").removeSuffix("\"")
            fields["BROADCAST_OPTION"] = Field(
                value = Either(broadcastName),
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

    // BUG WHEN ADDING BLOCK TO SUBSTACKABLE BLOCKS
    private fun addSerialBlock(block: Block, position: TokenPosition) {
        if (this.hasScopeEnded) {
            throw CompileException(position, "Cannot add statements after 'loop forever' or 'stop'")
        }

        val target = this.currentSprite ?: this.stage
        target.blocks[block.id] = block

        val lastBlock = this.lastBlock
        block.parent = lastBlock?.id
        if (lastBlock != null) {
            lastBlockMod?.invoke(block.id)
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
