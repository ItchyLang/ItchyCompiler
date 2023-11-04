package representation

data class Costume
(
    val assetId : String,
    val name : String,
    val md5ext : String,
    val dataFormat : String,
    val rotationCenterX : Int,
    val rotationCenterY : Int,
)