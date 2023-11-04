package net.itchy.ast.data

enum class ItchyType {
    VOID,
    STRING,
    BOOLEAN,
    DOUBLE;

    companion object {
        fun fromString(string: String): ItchyType? {
            for (type in ItchyType.entries) {
                if (type.name.lowercase() == string) {
                    return type
                }
            }
            return null
        }
    }
}