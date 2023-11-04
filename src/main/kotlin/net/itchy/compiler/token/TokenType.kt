package net.itchy.compiler.token

enum class TokenType(
    private val asString: String? = null
) {
    NEW_LINE("\n"),
    WHITESPACE,
    COMMENT,

    PLUS("+"),
    MINUS("-"),
    MULTIPLY("*"),
    DIVIDE("/"),
    MODULUS("%"),

    IDENTIFIER,
    STRING,
    NUMBER,
    TRUE("true"),
    FALSE("false"),

    EQUALS("=="),
    NOT_EQUALS("!="),
    LT_EQUALS("<="),
    GT_EQUALS(">="),
    LT("<"),
    GT(">"),
    NOT("!"),
    AND("&&"),
    OR("||"),

    ASSIGN("="),
    PLUS_ASSIGN("+="),
    MINUS_ASSIGN("-="),
    MULTIPLY_ASSIGN("*="),
    DIVIDE_ASSIGN("/="),

    LEFT_BRACKET("("),
    RIGHT_BRACKET(")"),
    LEFT_CURLY_BRACKET("{"),
    RIGHT_CURLY_BRACKET("}"),

    COLON(":"),
    COMMA(","),

    IF("if"),
    ELSE("else"),
    LOOP("loop"),
    FOREVER("forever"),
    COUNT("count"),
    UNTIL("until"),
    RETURN("return"),
    FUNC("func"),
    FAST("fast"),
    SPRITE("sprite"),
    WHEN("when"),
    LET("let"),

    EOF;

    fun asString(): String {
        return this.asString ?: this.toString()
    }
}