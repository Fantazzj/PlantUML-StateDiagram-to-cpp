package io.github.fantazzj.statediagram.converter.cxx

import io.github.fantazzj.statediagram.converter.Converter
import io.github.fantazzj.statediagram.structure.Diagram

class ConfigConverter(diagram: Diagram) : Converter(diagram) {

    private val variables = CxxConverter.getVariables(diagram.states)

    private val objects = CxxConverter.getObjects(diagram.states)

    fun convert(): String {
        val out = StringBuilder()
        addConfigContent(out)
        return out.toString()
    }

    private fun addConfigContent(out: StringBuilder) {
        out
            .also(::openIncludeGuards)
            .appendLine()
            .also(::defineHardwareSpecific)
            .appendLine()
            .also(::defineVariablesTypes)
            .appendLine()
            .also(::defineVariablesInitialValue)
            .appendLine()
            .also(::defineAdditionalAttributes)
            .appendLine()
            .also(::closeIncludeGuards)
    }

    private fun openIncludeGuards(out: StringBuilder) {
        out.appendLine("#ifndef ${getName().uppercase()}_CONFIG_HPP")
        out.appendLine("#define ${getName().uppercase()}_CONFIG_HPP")
    }

    private fun defineHardwareSpecific(out: StringBuilder) {
        out.appendLine("//for arduino:")
        out.appendLine("//#define ${getName().uppercase()}_MILLISECONDS millis()")
        out.appendLine("#ifndef ${getName().uppercase()}_MILLISECONDS")
        out.appendLine("#error \"didn't define the hardware specific ${getName().uppercase()}_MILLISECONDS function\"")
        out.appendLine("#endif")
    }

    private fun defineVariablesTypes(out: StringBuilder) {
        variables.forEach {
            out.appendLine("typedef int ${getName()}_${it}_t;")
        }

        objects.forEach {
            out.appendLine("typedef int ${getName()}_${it}_t;")
        }
    }

    private fun defineVariablesInitialValue(out: StringBuilder) {
        variables.forEach {
            out.appendLine("#define ${getName().uppercase()}_${it.uppercase()} 0")
        }
    }

    private fun defineAdditionalAttributes(out: StringBuilder) {
        out.appendLine("//if are unused can be safely deleted these two lines")
        out.appendLine("#define ${getName().uppercase()}_ADDITIONAL_PRIVATE_ATT void* foo_priv")
        out.appendLine("#define ${getName().uppercase()}_ADDITIONAL_PUBLIC_ATT void* foo_public")
    }

    private fun closeIncludeGuards(out: StringBuilder) {
        out.appendLine("#endif //${getName().uppercase()}_CONFIG_HPP")
    }

}
