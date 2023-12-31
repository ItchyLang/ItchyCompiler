package net.itchy.scratch.assets

import net.itchy.scratch.representation.Costume
import java.io.IOException
import java.net.URL
import java.nio.charset.Charset
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
    return if (Path(path).exists()) Path(path).readBytes() else null
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
        fileContents = getContentsFromURL(path) ?:TODO("URL does not exist, compile error")
    else
        fileContents = getContentsFromFile(path) ?:TODO("File does not exist, compile error")

    // Get file width and height
    var (width, height) = readImageDimensions(extension, fileContents)
    width = widthOverride?:width
    height = heightOverride?:height

    // Calculate MD5 hash
    val md = MessageDigest.getInstance("MD5")
    val hash = md.digest(fileContents).joinToString(separator = "") { "%02x".format(it) }

    return Costume(
        fileContents,
        hash,
        name,
        "$hash.$extension",
        extension,
        width / 2,
        height / 2
    )
}

fun readImageDimensions(extension: String, contents: ByteArray): Pair<Int, Int> {
    if (extension == "png") {
        val image = ImageIO.read(contents.inputStream())
        return image.width to image.height
    }

    // THIS IS JANK!!!!
    val widthRegex = """width="((?:[0-9]*[.])?[0-9]+)""".toRegex()
    val heightRegex = """height="((?:[0-9]*[.])?[0-9]+)""".toRegex()
    val svgContents = contents.toString(Charset.defaultCharset())
    val width = widthRegex.find(svgContents)!!.groupValues[1].toDouble().toInt()
    val height = heightRegex.find(svgContents)!!.groupValues[1].toDouble().toInt()
    return width to height
}