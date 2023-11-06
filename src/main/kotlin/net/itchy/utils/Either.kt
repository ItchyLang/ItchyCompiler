package net.itchy.utils

import java.lang.IllegalArgumentException
import kotlin.math.E

/**
 * Utility class for returning either one of two types.
 *
 * This is better than type casting with [Any] because the user can
 * be absolutely sure that it is one of 2 types.
 *
 * It is not possible to not have either value, one must be present and not null.
 *
 * This class cannot be constructed directly, see [Companion.left], and [Companion.right].
 *
 * @param A the left type.
 * @param B the right type.
 */
class Either<A: Any, B: Any> private constructor(
    private val left: A?,
    private val right: B?
) {
    /**
     * Checks whether the left value is present.
     *
     * @return whether the left is present.
     */
    fun isLeft(): Boolean {
        return left != null
    }

    /**
     * Checks whether the right value is present.
     *
     * @return whether the right is present.
     */
    fun isRight(): Boolean {
        return right != null
    }

    /**
     * Gets the value, this will return the **not null** value as [Any].
     *
     * @return the value.
     */
    fun get(): Any {
        return if (isLeft()) left() else right()
    }

    /**
     * This gets the left value, if it is null then it throws a [NullPointerException].
     *
     * @return the [left] value.
     */
    fun left(): A {
        return left!!
    }

    /**
     * This gets the right value, if it is null then it throws a [NullPointerException].
     *
     * @return the [right] value.
     */
    fun right(): B {
        return right!!
    }

    /**
     * This gets the left value or a default value if left is null.
     *
     * @param other the default value to fallback onto.
     * @return a value with the type [A].
     */
    fun leftOr(other: A): A {
        return left ?: other
    }

    /**
     * This gets the right value or a default value if right is null.
     *
     * @param other the default value to fallback onto.
     * @return a value with the type [B].
     */
    fun rightOr(other: B): B {
        return right ?: other
    }

    /**
     * This executes a lambda if the either holds the left value.
     *
     * @param block the lambda to execute.
     */
    inline fun ifLeft(block: (A) -> Unit) {
        if (isLeft()) {
            block(left())
        }
    }

    /**
     * This executes a lambda if the either holds the right value.
     *
     * @param block the lambda to execute.
     */
    inline fun ifRight(block: (B) -> Unit) {
        if (isRight()) {
            block(right())
        }
    }

    companion object {
        fun <A: Any, B: Any> left(left: A): Either<A, B> {
            return Either(left, null)
        }

        fun <A: Any, B: Any> right(right: B): Either<A, B> {
            return Either(null, right)
        }
    }
}

inline fun <reified A: Any, reified B: Any> Either(value: Any): Either<A, B> {
    return when (value) {
        is A -> Either.left(value)
        is B -> Either.right(value)
        else -> throw IllegalArgumentException()
    }
}
