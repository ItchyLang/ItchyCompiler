package util

data class VariantValue
private constructor
(
    val isDouble : Boolean,
    val dVal : Double,
    val sVal : String
)
{
    constructor (dValParam : Double) : this(true, dValParam, "")
    constructor (sValParam : String) : this(false, 0.0, sValParam)
}