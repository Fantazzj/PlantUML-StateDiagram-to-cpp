package io.github.fantazzj.statediagram.converter.cxx

import io.github.fantazzj.statediagram.converter.Converter
import io.github.fantazzj.statediagram.structure.Diagram

class EnumConverter(diagram: Diagram) : Converter(diagram) {

    fun convert(): String {
        val out = StringBuilder()
        addEnumContent(out)
        return out.toString()
    }

    private fun addEnumContent(out: StringBuilder) {
        out
            .also(::openIncludeGuards)
            .appendLine()
            .also(::writeEnum)
            .appendLine()
            .also(::closeIncludeGuards)
    }

    private fun openIncludeGuards(out: StringBuilder) {
        out.appendLine("#ifndef ${getName().uppercase()}_ENUM_HPP")
        out.appendLine("#define ${getName().uppercase()}_ENUM_HPP")
    }

    private fun writeEnum(out: StringBuilder) {
        out.appendLine("enum class ${getName()}State : int {")
        getStates().forEach { out.appendLine("\t${it.name},") }
        out.appendLine("};")
    }

    private fun closeIncludeGuards(out: StringBuilder) {
        out.appendLine("#endif //${getName().uppercase()}_ENUM_HPP")
    }

}
