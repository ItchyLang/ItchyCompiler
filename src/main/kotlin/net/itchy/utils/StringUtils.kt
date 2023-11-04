package net.itchy.utils

object StringUtils {
    fun String.escapeForRegex(): String {
        val sb = StringBuilder()
        var i = 0
        val len = this.length
        while (i < len) {
            val c = this[i]
            when (c) {
                '\u0000' -> {
                    sb.append("\\0")
                    i++
                    continue
                }
                '\n' -> {
                    sb.append("\\n")
                    i++
                    continue
                }
                '\r' -> {
                    sb.append("\\r")
                    i++
                    continue
                }
                '\t' -> {
                    sb.append("\\t")
                    i++
                    continue
                }
                '\\' -> {
                    sb.append("\\\\")
                    i++
                    continue
                }
                '^', '$', '?', '|', '*', '/', '+', '.', '(', ')', '[', ']', '{', '}' -> {
                    sb.append("\\").append(c)
                    i++
                    continue
                }
            }
            // Unicode
            if (c.code > 0xff) {
                sb.append("\\u").append(toHexString(c.code.toLong(), 4))
                i++
                continue
            }
            // Control character
            if (Character.isISOControl(c)) {
                sb.append("\\x").append(toHexString(c.code.toLong(), 2))
                i++
                continue
            }
            sb.append(c)
            i++
        }
        return sb.toString()
    }

    private fun toHexString(value: Long, length: Int): String {
        require(length >= 1) { "The minimum length of the returned string cannot be less than one." }
        return String.format("%0" + length + "x", value)
    }
}