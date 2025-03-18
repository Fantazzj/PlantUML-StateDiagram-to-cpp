package io.github.fantazzj.statediagram.converter.cxx

import io.github.fantazzj.statediagram.converter.Converter
import io.github.fantazzj.statediagram.structure.State
import java.nio.file.Path
import kotlin.io.path.createDirectory
import kotlin.io.path.exists

class CxxConverter(name: String, states: Collection<State>) : Converter(name, states) {

    private val cppConverter = CppConverter(name, states)
    private val hppConverter = HppConverter(name, states)
    private val enumConverter = EnumConverter(name, states)
    private val configConverter = ConfigConverter(name, states)

    override fun saveToDir(outDir: Path) {
        if (!outDir.exists())
            outDir.createDirectory()
        cppConverter.saveToDir(outDir)
        hppConverter.saveToDir(outDir)
        enumConverter.saveToDir(outDir)
        configConverter.saveToDir(outDir)
    }

    companion object {
        fun getVariables(states: List<State>): HashSet<String> {
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

            states.forEach { s ->
                s.getTransitions().forEach { t -> parseAndAdd(t.getCondition()) }
                s.getActions().forEach { a -> parseAndAdd(a.getAction()) }
            }

            return variables
        }
    }

}
