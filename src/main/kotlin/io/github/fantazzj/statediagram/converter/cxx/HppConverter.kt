package io.github.fantazzj.statediagram.converter.cxx

import io.github.fantazzj.statediagram.converter.Converter
import io.github.fantazzj.statediagram.structure.Diagram

class HppConverter(diagram: Diagram) : Converter(diagram) {

    private val variables = CxxConverter.getVariables(diagram.states)

    private val objects = CxxConverter.getObjects(diagram.states)

    fun convert(): String {
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
        out.appendLine("#ifndef ${getName().uppercase()}_HPP")
        out.appendLine("#define ${getName().uppercase()}_HPP")
    }

    private fun includeFiles(out: StringBuilder) {
        out.appendLine("#include \"${getName()}State.hpp\"")
        out.appendLine("#include \"${getName()}Config.hpp\"")
    }

    private fun openClass(out: StringBuilder) {
        out.appendLine("class ${getName()} {")
    }

    private fun writePublic(out: StringBuilder) {
        out.appendLine("public:")
    }

    private fun publicMethods(out: StringBuilder) {
        out.append("\texplicit ${getName()}(Timer& timer")
        objects.forEach {
            out.append(", ")
            out.append("${getName()}_${it}_t $it")
        }
        out.appendLine(");")
        out.appendLine("\tvoid autoCycle();")
        out.appendLine("\tvoid outputAnalysis();")
        out.appendLine("\t${getName()}State newState;")
    }

    private fun publicAttributes(out: StringBuilder) {
        variables.forEach {
            out.appendLine("\t${getName()}_${it}_t $it;")
        }

        out.appendLine("\t#ifdef ${getName().uppercase()}_ADDITIONAL_PUBLIC_ATT")
        out.appendLine("\t${getName().uppercase()}_ADDITIONAL_PUBLIC_ATT")
        out.appendLine("\t#endif")
    }

    private fun writePrivate(out: StringBuilder) {
        out.appendLine("private:")
    }

    private fun privateMethods(out: StringBuilder) {
        out.appendLine("\tvoid changeState(${getName()}State step);")
    }

    private fun privateAttributes(out: StringBuilder) {
        out.appendLine("\t${getName()}State oldState;")
        out.appendLine("\tunsigned long previousMillis;")
        out.appendLine("\tunsigned long elapsedMillis;")
        out.appendLine("\tTimer& timer;")

        objects.forEach {
            out.appendLine("\t${getName()}_${it}_t $it;")
        }

        out.appendLine("\t#ifdef ${getName().uppercase()}_ADDITIONAL_PRIVATE_ATT")
        out.appendLine("\t${getName().uppercase()}_ADDITIONAL_PRIVATE_ATT")
        out.appendLine("\t#endif")
    }

    private fun closeClass(out: StringBuilder) {
        out.appendLine("};")
    }

    private fun includeGuardsBottom(out: StringBuilder) {
        out.appendLine("#endif//${getName().uppercase()}_HPP")
    }

}
