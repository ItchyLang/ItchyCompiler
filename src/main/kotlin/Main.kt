import net.itchy.utils.compileAndBundle
import net.itchy.utils.compileAndBundleFile

fun main()
{
    compileAndBundle("test", """
    sprite X {
        func invert(should: boolean) {
            return !should
        }
        
        func sayy(what: string) {
            say(what)
        }
        
        when init {
            load_costume("c1", "abby-a.svg")
            load_backdrop("b1", "backdrop.svg")
        
            if (invert(false)) {
                sayy("Hello, world!")
            }
            
            let x: double = 1
            loop count 5 {
                x += 1
            }
            
            sayy(x)
        }
    }
    """.trimIndent(), ".")
}