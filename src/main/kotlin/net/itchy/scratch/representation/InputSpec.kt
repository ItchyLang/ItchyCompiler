package net.itchy.scratch.representation

import net.itchy.utils.Either

data class InputSpec
(
    val inputType: Int = 4,
    val value: Either<String, Double> = Either(0.0),
    val id: String? = null
)