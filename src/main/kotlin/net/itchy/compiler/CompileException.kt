package net.itchy.compiler

import net.itchy.compiler.token.TokenPosition

class CompileException(
    position: TokenPosition,
    message: String = "Failed to compile (${position})"
): RuntimeException(message)