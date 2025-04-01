package io.github.fantazzj.statediagram.converter.cxx

import io.github.fantazzj.statediagram.converter.Converter
import io.github.fantazzj.statediagram.structure.State
import java.io.File
import java.io.PrintWriter
import java.nio.file.Path

class ConfigConverter(name: String, states: Collection<State>) : Converter(name, states) {

    private val variables = CxxConverter.getVariables(states)

    private val objects = CxxConverter.getObjects(states)

    override fun saveToDir(outDir: Path) {
        val configFile = File("$outDir/${getName()}" + "Config.hpp")
        configFile.createNewFile()
        addConfigContent(configFile)
    }

    private fun addConfigContent(configFile: File) =
        configFile.printWriter().use { out ->
            out.println("#ifndef ${getName().uppercase()}_CONFIG_HPP")
            out.println("#define ${getName().uppercase()}_CONFIG_HPP")
            out.println()
            defineHardwareSpecific(out)
            out.println()
            defineVariablesTypes(out)
            out.println()
            defineVariablesInitialValue(out)
            out.println()
            defineAdditionalAttributes(out)
            out.println()
            out.println("#endif //${getName().uppercase()}_CONFIG_HPP")
        }

    private fun defineHardwareSpecific(out: PrintWriter) {
        out.println("//for arduino:")
        out.println("//#define MILLISECONDS millis()")
        out.println("#ifndef MILLISECONDS")
        out.println("#error \"didn't define the hardware specific MILLISECONDS function\"")
        out.println("#endif")
    }

    private fun defineVariablesTypes(out: PrintWriter) {
        variables.forEach { v ->
            out.println("typedef int ${v + "_t"};")
        }

        objects.forEach { v ->
            out.println("typedef int ${v + "_t"};")
        }
    }

    private fun defineVariablesInitialValue(out: PrintWriter) {
        variables.forEach { v ->
            out.println("#define ${v.uppercase()} 0")
        }
    }

    private fun defineAdditionalAttributes(out: PrintWriter) {
        out.println("#define ADDITIONAL_PRIVATE_ATT void foo_priv")
        out.println("#define ADDITIONAL_PUBLIC_ATT void foo_public")
    }

}
