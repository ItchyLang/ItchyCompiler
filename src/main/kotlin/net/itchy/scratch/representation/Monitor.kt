package representation

import util.VariantValue

data class Monitor
private constructor
(
    val id : String,
    val opcode : String,
    val params : HashMap<String, String>,
    val spriteName : String?,
    val mode : String = "default"
)
{
    constructor(isList : Boolean, id : String, name : String, sprite : String?) : this
    (
        id,
        if (isList) "data_listcontents" else "data_variable",
        if (isList) hashMapOf("LIST" to name) else hashMapOf("VARIABLE" to name),
        sprite
    )

    val value = VariantValue(0.0)
    val width = 0
    val height = 0
    val x = 0
    val y = 0
    val visible= false
    val sliderMin = 0
    val sliderMax = 100
    val isDiscrete = true
}