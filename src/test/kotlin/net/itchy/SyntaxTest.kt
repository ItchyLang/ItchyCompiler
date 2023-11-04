package net.itchy

import net.itchy.TestHelper.compiles
import net.itchy.TestHelper.doesntCompile
import org.junit.jupiter.api.Test

class SyntaxTest {
    @Test
    fun testSprites() {
        compiles("sprite X { }")
        compiles("sprite X { when init { } }")
        compiles("sprite X { } sprite Y { }")
        doesntCompile("sprite { }")
        doesntCompile("sprite X")
        doesntCompile("sprite X {")

        compiles(
            """
            sprite X {
                func foo() {
                
                }
            }
            """.trimIndent()
        )
        compiles(
            """
            sprite X 
            {
                func foo() 
                {
                
                }
            }
            """.trimIndent()
        )
        compiles(
            """
            sprite X 
            {
                when init 
                {
                
                }
            }
            """.trimIndent()
        )
    }

    @Test
    fun testFunctions() {
        doesntCompile("fun X() { }")
        doesntCompile("func () { }")
        doesntCompile("func X()")
        doesntCompile("func X( { }")
        doesntCompile("func X) { }")
        doesntCompile("func X")

        compiles("func X() { }")
        compiles("func X(): void { }")
        compiles("func X(): string { }")
        compiles("func X(): double { }")
        compiles("func X(): boolean { }")
        doesntCompile("func X(): foo { }")

        compiles("func Y(a: string) { }")
        compiles("func Y(a: string, b: boolean) { }")
        compiles("func Y(a: string, b: boolean, ) { }")
        compiles("func Y(a: double): string { }")

        compiles(
            """
            func Z() {
            
            }
            """.trimIndent()
        )
        compiles(
            """
            func Z() 
            {
            
            }
            """.trimIndent()
        )
        compiles(
            """
            func Z
            (
                a: string
            ): void 
            {
            
            }
            """.trimIndent()
        )
        compiles(
            """
            func Z()
            : string {
            
            }
            """.trimIndent()
        )
    }

    @Test
    fun testWhen() {
        compiles("when init { }")
        compiles("when receive \"message\" { }")
        compiles(
            """
            when receive "message" { 
            
            }
            """.trimIndent()
        )
        compiles(
            """
            when receive "message" 
            { 
            
            }
            """.trimIndent()
        )
        doesntCompile("when { }")
        doesntCompile("when init")
        doesntCompile("when \"foobar\"")
    }

    @Test
    fun testLet() {
        compiles("let x: double = 10")
        compiles("let x: string = \"\"")
        compiles("let x: double")
        compiles("let x: string = 10")
        compiles("let x: void")

        doesntCompile("x: string")
        doesntCompile("let x")
        doesntCompile("let x = 10")
    }

    @Test
    fun testExpressions() {
        compiles(
            """
            when init {
                10 + 10
            }
            """.trimIndent()
        )
        compiles(
            """
            when init {
                10 - 10
            }
            """.trimIndent()
        )
        compiles(
            """
            when init {
                10 * 10
            }
            """.trimIndent()
        )
        compiles(
            """
            when init {
                10 / 10
            }
            """.trimIndent()
        )
        compiles(
            """
            when init {
                10 % 10
            }
            """.trimIndent()
        )
    }
}