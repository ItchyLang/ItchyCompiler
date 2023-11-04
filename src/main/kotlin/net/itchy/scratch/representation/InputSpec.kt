package representation

import util.VariantValue

data class InputSpec
(
    val inputType : Int,
    val value : VariantValue,
    val id : String?
)