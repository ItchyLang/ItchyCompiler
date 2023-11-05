package net.itchy.scratch.representation

data class Costume
(
    @Suppress("ArrayInDataClass")
    @Transient val content: ByteArray,
    val assetId : String,
    val name : String,
    val md5ext : String,
    val dataFormat : String,
    val rotationCenterX : Int,
    val rotationCenterY : Int,
)