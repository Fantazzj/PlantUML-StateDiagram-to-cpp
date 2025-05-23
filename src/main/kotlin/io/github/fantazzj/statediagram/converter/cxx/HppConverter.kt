package io.github.fantazzj.statediagram.converter.cxx

import io.github.fantazzj.statediagram.converter.Converter
import io.github.fantazzj.statediagram.structure.State
import java.nio.file.Path
import java.io.File
import java.io.PrintWriter

class HppConverter(name: String, states: Collection<State>) : Converter(name, states) {

    private val variables = CxxConverter.getVariables(states)

    private val objects = CxxConverter.getObjects(states)

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
        out.println("#include \"${getName()}State.hpp\"")
        out.println("#include \"${getName()}Config.hpp\"")
    }

    private fun publicMethods(out: PrintWriter) {
        out.print("\texplicit ${getName()}(")
        objects.forEach {
            out.print("${getName()}_${it}_t $it")
            if (it != objects.last())
                out.print(", ")
        }
        out.println(");")
        out.println("\tvoid autoCycle();")
        out.println("\tvoid outputAnalysis();")
        out.println("\t${getName()}State newState;")
    }

    private fun publicAttributes(out: PrintWriter) {
        variables.forEach {
            out.println("\t${getName()}_${it}_t $it;")
        }

        objects.forEach {
            out.println("\t${getName()}_${it}_t $it;")
        }

        out.println("\t#ifdef ${getName().uppercase()}_ADDITIONAL_PUBLIC_ATT")
        out.println("\t${getName().uppercase()}_ADDITIONAL_PUBLIC_ATT")
        out.println("\t#endif")
    }

    private fun privateMethods(out: PrintWriter) {
        out.println("\tvoid changeState(${getName()}State step);")
    }

    private fun privateAttributes(out: PrintWriter) {
        out.println("\t${getName()}State oldState;")
        out.println("\tunsigned long previousMillis;")
        out.println("\tunsigned long elapsedMillis;")
        out.println("\t#ifdef ${getName().uppercase()}_ADDITIONAL_PRIVATE_ATT")
        out.println("\t${getName().uppercase()}_ADDITIONAL_PRIVATE_ATT")
        out.println("\t#endif")
    }

    private fun includeGuardsBottom(out: PrintWriter) {
        out.println("#endif//${getName().uppercase()}_HPP")
    }

}
