package io.github.fantazzj.statemachine.converter

import io.github.fantazzj.statemachine.structure.State
import java.io.File
import java.io.PrintWriter
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.createDirectory
import kotlin.io.path.exists

class CppConverter(private val name: String, private val states: Collection<State>) : Converter {

    override fun saveToDir(outDir: Path) {
        if (!outDir.exists())
            outDir.createDirectory()
        val hppFile = File("$outDir/$name.hpp")
        val cppFile = File("$outDir/$name.cpp")
        hppFile.createNewFile()
        cppFile.createNewFile()
        addCppContent(cppFile)
        addHppContent(hppFile)
    }

    private fun addCppContent(cppFile: File) =
        cppFile.printWriter().use { out ->
            writeInclude(out)
            out.println()
            writeConstructor(out)
            out.println()
            writeAutoCycle(out)
            out.println()
            writeOutputAnalysis(out)
            out.println()
            writeChangeState(out)
            out.close()
        }

    private fun writeInclude(out: PrintWriter) {

    }

    private fun writeOutputAnalysis(out: PrintWriter) {

    }

    private fun writeConstructor(out: PrintWriter) {

    }

    private fun writeChangeState(out: PrintWriter) {

    }

    private fun writeAutoCycle(out: PrintWriter) {

    }

    private fun addHppContent(hppFile: File) =
        hppFile.printWriter().use { out ->
            includeGuardsTop(out)
            out.println()
            includeFiles(out)
            out.println()
            out.println("class $name {")
            out.println("public:")
            publicMethods(out)
            publicAttributes(out)
            out.println()
            out.println("private:")
            privateMethods(out)
            privateAttributes(out)
            out.println("};")
            out.println()
            includeGuardsBottom(out)
            out.close()
        }

    private fun includeGuardsTop(out: PrintWriter) {
        out.println("#ifndef ${name.uppercase()}_HPP")
        out.println("#define ${name.uppercase()}_HPP")
    }

    private fun includeFiles(out: PrintWriter) {
    }

    private fun publicMethods(out: PrintWriter) {

    }

    private fun publicAttributes(out: PrintWriter) {

    }

    private fun privateMethods(out: PrintWriter) {

    }

    private fun privateAttributes(out: PrintWriter) {

    }

    private fun includeGuardsBottom(out: PrintWriter) {
        out.println("#endif//${name.uppercase()}_HPP")
    }

}
