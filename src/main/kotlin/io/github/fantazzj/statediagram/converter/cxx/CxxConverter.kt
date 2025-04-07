package io.github.fantazzj.statediagram.converter.cxx

import io.github.fantazzj.statediagram.converter.Converter
import io.github.fantazzj.statediagram.structure.State
import java.nio.file.Path
import kotlin.io.path.Path
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
        if (!Path(outDir.toString(), getName()+"Config.hpp").exists())
            configConverter.saveToDir(outDir)
    }

    companion object {
        private val parserRegex = Regex("\\b[a-z][\\w.]*")

        fun getVariables(states: Collection<State>): Collection<String> {
            val variables = HashSet<String>()
            val parseAndAdd = { text: String ->
                parserRegex.findAll(text)
                    .filter { m -> m.value !in setOf("true", "false", "elapsedMillis") }
                    .filter { m -> !m.value.contains('.') }
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

        fun getObjects(states: Collection<State>): Collection<String> {
            val objects = HashSet<String>()
            val parseAndAdd = { text: String ->
                parserRegex.findAll(text)
                    .forEach { m ->
                        if (m.value.contains('.'))
                            objects.add(m.value.split('.').first())
                    }
            }

            states.forEach { s ->
                s.getTransitions().forEach { t -> parseAndAdd(t.getCondition()) }
                s.getActions().forEach { a -> parseAndAdd(a.getAction()) }
            }

            return objects
        }
    }

}
