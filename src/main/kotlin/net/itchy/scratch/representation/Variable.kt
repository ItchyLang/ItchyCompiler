package net.itchy.scratch.representation

import net.itchy.utils.Either

data class Variable(
    val name: String,
    val value: Either<String, Double>
)