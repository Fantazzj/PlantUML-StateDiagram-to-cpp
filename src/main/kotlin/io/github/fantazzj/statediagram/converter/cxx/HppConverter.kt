package io.github.fantazzj.statediagram.converter.cxx

import io.github.fantazzj.statediagram.converter.Converter
import io.github.fantazzj.statediagram.structure.Diagram

class HppConverter(private val diagram: Diagram) : Converter {

    private val variables = CxxConverter.getVariables(diagram.states)

    private val objects = CxxConverter.getObjects(diagram.states)

    override fun convert(diagram: Diagram): String {
        val out = StringBuilder()
        addHppContent(out)
        return out.toString()
    }

    private fun addHppContent(out: StringBuilder) {
        out
            .also(::includeGuardsTop)
            .appendLine()
            .also(::includeFiles)
            .appendLine()
            .also(::openClass)
            .also(::writePublic)
            .also(::publicMethods)
            .also(::publicAttributes)
            .appendLine()
            .also(::writePrivate)
            .also(::privateMethods)
            .also(::privateAttributes)
            .also(::closeClass)
            .appendLine()
            .also(::includeGuardsBottom)
    }

    private fun includeGuardsTop(out: StringBuilder) {
        out.appendLine("#ifndef ${diagram.name.uppercase()}_HPP")
        out.appendLine("#define ${diagram.name.uppercase()}_HPP")
    }

    private fun includeFiles(out: StringBuilder) {
        out.appendLine("#include \"${diagram.name}State.hpp\"")
        out.appendLine("#include \"${diagram.name}Config.hpp\"")
    }

    private fun openClass(out: StringBuilder) {
        out.appendLine("class ${diagram.name} {")
    }

    private fun writePublic(out: StringBuilder) {
        out.appendLine("public:")
    }

    private fun publicMethods(out: StringBuilder) {
        out.append("\texplicit ${diagram.name}(Timer& timer")
        objects.forEach {
            out.append(", ")
            out.append("${diagram.name}_${it}_t $it")
        }
        out.appendLine(");")
        out.appendLine("\tvoid autoCycle();")
        out.appendLine("\tvoid outputAnalysis();")
        out.appendLine("\t${diagram.name}State newState;")
    }

    private fun publicAttributes(out: StringBuilder) {
        variables.forEach {
            out.appendLine("\t${diagram.name}_${it}_t $it;")
        }

        out.appendLine("\t#ifdef ${diagram.name.uppercase()}_ADDITIONAL_PUBLIC_ATT")
        out.appendLine("\t${diagram.name.uppercase()}_ADDITIONAL_PUBLIC_ATT")
        out.appendLine("\t#endif")
    }

    private fun writePrivate(out: StringBuilder) {
        out.appendLine("private:")
    }

    private fun privateMethods(out: StringBuilder) {
        out.appendLine("\tvoid changeState(${diagram.name}State step);")
    }

    private fun privateAttributes(out: StringBuilder) {
        out.appendLine("\t${diagram.name}State oldState;")
        out.appendLine("\tunsigned long previousMillis;")
        out.appendLine("\tunsigned long elapsedMillis;")
        out.appendLine("\tTimer& timer;")

        objects.forEach {
            out.appendLine("\t${diagram.name}_${it}_t $it;")
        }

        out.appendLine("\t#ifdef ${diagram.name.uppercase()}_ADDITIONAL_PRIVATE_ATT")
        out.appendLine("\t${diagram.name.uppercase()}_ADDITIONAL_PRIVATE_ATT")
        out.appendLine("\t#endif")
    }

    private fun closeClass(out: StringBuilder) {
        out.appendLine("};")
    }

    private fun includeGuardsBottom(out: StringBuilder) {
        out.appendLine("#endif//${diagram.name.uppercase()}_HPP")
    }

}
