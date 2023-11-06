package net.itchy.scratch.representation

import net.itchy.scratch.representation.constants.ShadowState
import net.itchy.utils.Either

class Input private constructor(
    val shadowState: Int,
    val actualInput: Either<String, InputGenerator>?,
    val obscuredShadow: Either<String, InputGenerator>?
) {
    fun withShadow(): Input {
        return Input(ShadowState.SHADOW.id, this.actualInput, null)
    }

    fun withoutShadow(): Input {
        return Input(ShadowState.NO_SHADOW.id, this.actualInput, null)
    }

    companion object {
        /**
         * This creates a literal number input.
         *
         * @param literal The literal number to use.
         * @return The input.
         */
        fun number(literal: Double): Input {
            return Input(
                ShadowState.SHADOW.id,
                Either(InputGenerator.number(literal)),
                null
            )
        }

        /**
         * This creates a literal string input.
         *
         * @param literal The literal string to use.
         * @return The input.
         */
        fun string(literal: String): Input {
            return Input(
                ShadowState.SHADOW.id,
                Either(InputGenerator.string(literal)),
                null
            )
        }

        /**
         * This creates a stub input, used in blocks
         * which have sub-stacks but don't require them.
         *
         * @return The input.
         */
        fun nothing(): Input {
            return Input(ShadowState.SHADOW.id, null, null)
        }

        /**
         * This creates a block input, it uses
         * the output of a given block to use to generate
         * the input.
         *
         * @param id The id of the block to use.
         * @return The input.
         */
        fun block(id: String): Input {
            return Input(
                ShadowState.OBSCURED_SHADOW.id,
                Either(id),
                Either(InputGenerator.number())
            )
        }

        /**
         * This creates a substack block input, it uses
         * the output of a given block to use to generate
         * the input.
         *
         * @param id The id of the block to use.
         * @return The input.
         */
        fun substack(id: String): Input {
            return Input(
                ShadowState.SHADOW.id,
                Either(id),
                null
            )
        }

        /**
         * This creates a variable input, it uses
         * the value of a given variable to use to generate
         * the input.
         *
         * @param name The name of the variable.
         * @param id The id of the variable.
         * @return The input.
         */
        fun variable(name: String, id: String): Input {
            return Input(
                ShadowState.OBSCURED_SHADOW.id,
                Either(InputGenerator.variable(name, id)),
                Either(InputGenerator.number())
            )
        }

        /**
         * This uses a broadcast as an input.
         *
         * @param name The name of the broadcast.
         * @param id The id of the broadcast.
         * @return The input.
         */
        fun broadcast(name: String, id: String): Input {
            return Input(
                ShadowState.SHADOW.id,
                Either(InputGenerator.broadcast(name, id)),
                null
            )
        }
    }
}