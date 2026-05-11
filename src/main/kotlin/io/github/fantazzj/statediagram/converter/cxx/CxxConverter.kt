package io.github.fantazzj.statediagram.converter.cxx

import io.github.fantazzj.statediagram.converter.Converter
import io.github.fantazzj.statediagram.structure.Diagram
import io.github.fantazzj.statediagram.structure.State
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.createDirectory
import kotlin.io.path.exists

class CxxConverter(diagram: Diagram) : Converter(diagram) {

    private val cppConverter = CppConverter(diagram)
    private val hppConverter = HppConverter(diagram)
    private val enumConverter = EnumConverter(diagram)
    private val configConverter = ConfigConverter(diagram)

    override fun saveToDir(outDir: Path) {
        if (!outDir.exists())
            outDir.createDirectory()
        cppConverter.saveToDir(outDir)
        hppConverter.saveToDir(outDir)
        enumConverter.saveToDir(outDir)
        if (!Path(outDir.toString(), getName() + "Config.hpp").exists())
            configConverter.saveToDir(outDir)
    }

    companion object {
        private val parserRegex = Regex("\\b[a-z][\\w.]*")

        fun getVariables(states: Collection<State>): Collection<String> {
            val variables = HashSet<String>()
            val parseAndAdd = { text: String ->
                parserRegex.findAll(text)
                    .filter { it.value !in setOf("true", "false", "elapsedMillis") }
                    .filter { !it.value.contains('.') }
                    .forEach { variables.add(it.value) }
            }

            states.forEach { s ->
                s.transitions.forEach { t -> parseAndAdd(t.condition) }
                s.actions.forEach { a -> parseAndAdd(a.action) }
            }

            return variables
        }

        fun getObjects(states: Collection<State>): Collection<String> {
            val objects = HashSet<String>()
            val parseAndAdd = { text: String ->
                parserRegex.findAll(text)
                    .forEach {
                        if (it.value.contains('.'))
                            objects.add(it.value.split('.').first())
                    }
            }

            states.forEach { s ->
                s.transitions.forEach { t -> parseAndAdd(t.condition) }
                s.actions.forEach { a -> parseAndAdd(a.action) }
            }

            return objects
        }
    }

}
