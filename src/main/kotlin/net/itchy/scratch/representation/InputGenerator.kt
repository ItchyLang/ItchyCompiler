package net.itchy.scratch.representation

import net.itchy.scratch.representation.constants.InputType
import net.itchy.utils.Either

class InputGenerator private constructor(
    val inputType: Int,
    val value: Either<String, Double>,
    val id: String?
) {
    companion object {
        /**
         * This method takes in a number [literal] and
         * returns a [InputGenerator] which can be
         * provided into an [Input] object.
         *
         * @param literal The number literal.
         * @return The generator.
         */
        fun number(literal: Double = 0.0): InputGenerator {
            return InputGenerator(InputType.NUMBER.id, Either(literal), null)
        }

        /**
         * This method takes in a string [literal] and
         * returns a [InputGenerator] which can be
         * provided into an [Input] object.
         *
         * @param literal The string literal.
         * @return The generator.
         */
        fun string(literal: String = ""): InputGenerator {
            return InputGenerator(InputType.STRING.id, Either(literal), null)
        }

        /**
         * This method takes in a [name] and [id] for
         * a broadcast input and returns a [InputGenerator]
         * which can be provided into an [Input] object.
         *
         * @param name The name of the broadcast.
         * @param id The id of the broadcast.
         * @return The generator.
         */
        fun broadcast(name: String, id: String): InputGenerator {
            return InputGenerator(InputType.BROADCAST.id, Either(name), id)
        }

        /**
         * This method takes in a [name] and [id] for
         * a variable input and returns a [InputGenerator]
         * which can be provided into an [Input] object.
         *
         * @param name The name of the variable.
         * @param id The id of the variable.
         * @return The generator.
         */
        fun variable(name: String, id: String): InputGenerator {
            return InputGenerator(InputType.VARIABLE.id, Either(name), id)
        }
    }
}