import net.itchy.utils.compileAndBundle
import net.itchy.utils.compileAndBundleFile

fun main()
{
    compileAndBundle("test", """
func factorial(n: double): double {
    if (n <= 1) {
        return 1
    } 
    return n * factorial(n - 1)
}

sprite X {
    when init {
        load_costume("c1", "abby-a.svg")
        load_backdrop("b1", "backdrop.svg")

        say(factorial(10))
    }
}
    """.trimIndent(), ".")
}