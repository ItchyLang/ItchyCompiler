package net.itchy.scratch.representation

import net.itchy.utils.VariantValue

data class InputSpec
(
    val inputType : Int = 4,
    val value : VariantValue = VariantValue(0.0),
    val id : String? = null
)