package net.itchy.scratch.representation

import net.itchy.utils.Either

data class Input
(
    val shadowState : Int,
    val actualInput : Either<String, InputSpec>,
    val obscuredShadow : Either<String, InputSpec>?
)