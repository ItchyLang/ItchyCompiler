package representation

data class Input
(
    val shadowState : Int,
    val actualInput : util.Either<String, InputSpec>,
    val obscuredShadow : util.Either<String, InputSpec>?
)