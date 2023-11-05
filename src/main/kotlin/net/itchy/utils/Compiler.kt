package net.itchy.utils

import net.itchy.compiler.lexer.Lexer
import net.itchy.compiler.parser.Parser
import net.itchy.scratch.ScratchGenerator
import net.itchy.scratch.bundling.bundle
import kotlin.io.path.*

fun compileAndBundleFile(inPath: String) {
    val path = Path(inPath)
    compileAndBundle(path.nameWithoutExtension, path.readText(), path.parent.absolutePathString())
}

fun compileAndBundle(name: String, source: String, outPath: String) {
    val ast = Parser(Lexer(source).lex()).parse()
    bundle(name, ScratchGenerator().generate(ast), outPath)
}