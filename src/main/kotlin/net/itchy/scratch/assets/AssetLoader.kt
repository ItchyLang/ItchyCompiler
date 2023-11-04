package assets

import representation.Costume
import java.io.IOException
import java.net.URL
import java.security.MessageDigest
import javax.imageio.ImageIO
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.readBytes

fun getContentsFromURL(url : String) : ByteArray?
{
    return try
    {
        val input = URL(url).openStream()
        input.buffered().use{ it.readBytes() }
    } catch (exception : IOException)
    {
        null
    }
}

fun getContentsFromFile(path : String) : ByteArray?
{
    if (Path(path).exists())
        return Path(path).readBytes()
    else
        return null
}

fun loadCostume(name : String, path : String,
    widthOverride : Int? = null, heightOverride : Int? = null) : Costume
{
    // Get file extension
    val extension = path.substringAfterLast('.')
    if (extension !in arrayOf("svg", "png"))
        TODO("Maybe scratch accepts other files?")

    // Read file
    val fileContents : ByteArray?
    if (path.startsWith("http://") || path.startsWith("https://"))
        fileContents = getContentsFromURL(path)?:TODO("URL does not exist, compile error")
    else
        fileContents = getContentsFromFile(path)?:TODO("File does not exist, compile error")

    // Get file width and height
    val width : Int
    val height : Int
    val img = ImageIO.read(fileContents.inputStream())
    width = widthOverride ?: img.width
    height = heightOverride ?: img.height

    // Calculate MD5 hash
    val md = MessageDigest.getInstance("MD5")
    val hash = md.digest(fileContents).joinToString(separator = "") { "%02x".format(it) }

    return Costume(hash, name, "$hash.$extension", extension, width / 2, height / 2)
}