package net.itchy.utils

object StringUtils {
    fun String.toNumber(): Double {
        var s = this
        require(s.isNotBlank()) { "Cannot convert an empty string to a number" }

        // First check if the value is negative.
        val isNegative = s[0] == '-'
        if (isNegative) {
            // If the string is negative, we remove the first character.
            s = s.substring(1)
        }

        // Check for hex
        val result = if (s.startsWith("0x")) s.substring(2).toLong(16).toDouble() else s.toDouble()
        return if (isNegative) -result else result
    }

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