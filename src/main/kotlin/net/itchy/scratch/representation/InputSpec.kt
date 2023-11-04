package net.itchy.scratch.representation

import net.itchy.utils.VariantValue

data class InputSpec
(
    val inputType : Int,
    val value : VariantValue,
    val id : String? = null
)