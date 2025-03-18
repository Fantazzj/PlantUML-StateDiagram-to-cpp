package io.github.fantazzj.statediagram.converter.cxx

import io.github.fantazzj.statediagram.converter.Converter
import io.github.fantazzj.statediagram.structure.State
import java.io.File
import java.io.PrintWriter
import java.nio.file.Path

class ConfigConverter(name: String, states: Collection<State>) : Converter(name, states) {

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
            defineVariablesInitialValue(out)
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

    private fun defineVariablesInitialValue(out: PrintWriter) {
        val re = Regex("[A-Za-z]\\w*")
        val variables = HashSet<String>()
        val parseAndAdd = { text: String ->
            re.findAll(text)
                .filter { m -> m.value !in setOf("true", "false") }
                .filter { m -> !m.value.first().isUpperCase() }
                .forEach { m ->
                    variables.add(m.value)
                }
        }

        getStates().forEach { s ->
            s.getTransitions().forEach { t -> parseAndAdd(t.getCondition()) }
            s.getActions().forEach { a -> parseAndAdd(a.getAction()) }
        }

        variables.forEach { v ->
            out.println("#define ${v.uppercase()}")
        }
    }

}
