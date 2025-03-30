package io.github.fantazzj.statediagram.converter.cxx

import io.github.fantazzj.statediagram.converter.Converter
import io.github.fantazzj.statediagram.structure.State
import java.nio.file.Path
import java.io.File
import java.io.PrintWriter

class HppConverter(name: String, states: Collection<State>) : Converter(name, states) {

    override fun saveToDir(outDir: Path) {
        val hppFile = File("$outDir/${getName()}.hpp")
        hppFile.createNewFile()
        addHppContent(hppFile)
    }

    private fun addHppContent(hppFile: File) =
        hppFile.printWriter().use { out ->
            includeGuardsTop(out)
            out.println()
            includeFiles(out)
            out.println()
            out.println("class ${getName()} {")
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
        out.println("#ifndef ${getName().uppercase()}_HPP")
        out.println("#define ${getName().uppercase()}_HPP")
    }

    private fun includeFiles(out: PrintWriter) {
        out.println("#include \"${getName()}" + "Enum.hpp\"")
        out.println("#include \"${getName()}" + "Config.hpp\"")
    }

    private fun publicMethods(out: PrintWriter) {
        out.println("\t${getName()}();")
        out.println("\tvoid autoCycle();")
        out.println("\tvoid outputAnalysis();")
        out.println("\tState newState;")
    }

    private fun publicAttributes(out: PrintWriter) {
        val variables = CxxConverter.getVariables(getStates())

        variables.forEach { v ->
            out.println("\t${v + "_t"} $v = ${v.uppercase()};")
        }
    }

    private fun privateMethods(out: PrintWriter) {
        out.println("\tvoid changeState(State step);")
    }

    private fun privateAttributes(out: PrintWriter) {
        out.println("\tState oldState;")
        out.println("\tunsigned long previousMillis;")
        out.println("\tunsigned long elapsedMillis;")
    }

    private fun includeGuardsBottom(out: PrintWriter) {
        out.println("#endif//${getName().uppercase()}_HPP")
    }

}
