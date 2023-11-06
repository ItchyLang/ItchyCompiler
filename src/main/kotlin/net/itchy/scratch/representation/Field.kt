package net.itchy.scratch.representation

import net.itchy.utils.Either

data class Field(
    val value: Either<String, Double>,
    val id: String? = null
)