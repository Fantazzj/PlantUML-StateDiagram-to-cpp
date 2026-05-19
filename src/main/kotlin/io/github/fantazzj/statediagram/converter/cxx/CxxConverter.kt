package io.github.fantazzj.statediagram.converter.cxx

import io.github.fantazzj.statediagram.structure.Diagram
import io.github.fantazzj.statediagram.structure.State
import java.io.File
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.createDirectory
import kotlin.io.path.exists

class CxxConverter(private val diagram: Diagram) {

    fun saveToDir(outDir: Path) {
        if (!outDir.exists())
            outDir.createDirectory()

        val cppFile = File("$outDir/${diagram.name}.cpp")
        cppFile.createNewFile()
        cppFile.printWriter().use {
            it.print(CppConverter.convert(diagram))
            it.close()
        }

        val hppFile = File("$outDir/${diagram.name}.hpp")
        hppFile.createNewFile()
        hppFile.printWriter().use {
            it.print(HppConverter.convert(diagram))
            it.close()
        }

        val enumFile = File("$outDir/${diagram.name}State.hpp")
        enumFile.createNewFile()
        enumFile.printWriter().use {
            it.print(EnumConverter.convert(diagram))
            it.close()
        }

        if (!Path(outDir.toString(), diagram.name + "Config.hpp").exists()) {
            val configFile = File("$outDir/${diagram.name}Config.hpp")
            configFile.createNewFile()
            configFile.printWriter().use {
                it.print(ConfigConverter.convert(diagram))
                it.close()
            }
        }
    }

    companion object {
        private val parserRegex = Regex("\\b[a-z][\\w.]*")

        fun getVariables(states: Collection<State>): Collection<String> {
            val strings =
                states.flatMap { it.transitions }.map { it.condition } + states.flatMap { it.actions }.map { it.action }

            val variables = strings
                .asSequence()
                .flatMap { parserRegex.findAll(it) }
                .map { it.value }
                .filter { it !in setOf("true", "false", "elapsedMillis") }
                .filterNot { it.contains('.') }
                .toHashSet()

            return variables
        }

        fun getObjects(states: Collection<State>): Collection<String> {
            val strings =
                states.flatMap { it.transitions }.map { it.condition } + states.flatMap { it.actions }.map { it.action }

            val objects = strings
                .asSequence()
                .flatMap { parserRegex.findAll(it) }
                .map { it.value }
                .filter { it !in setOf("true", "false", "elapsedMillis") }
                .filter { it.contains('.') }
                .map { it.split('.').first() }
                .toHashSet()

            return objects
        }
    }

}
