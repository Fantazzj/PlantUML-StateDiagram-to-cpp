package io.github.fantazzj.statediagram.converter.cxx

import io.github.fantazzj.statediagram.converter.Converter
import io.github.fantazzj.statediagram.structure.Diagram
import io.github.fantazzj.statediagram.structure.State
import java.io.File
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.createDirectory
import kotlin.io.path.exists

class CxxConverter(diagram: Diagram) : Converter(diagram) {

    private val cppConverter = CppConverter(diagram)
    private val hppConverter = HppConverter(diagram)
    private val enumConverter = EnumConverter(diagram)
    private val configConverter = ConfigConverter(diagram)

    fun saveToDir(outDir: Path) {
        if (!outDir.exists())
            outDir.createDirectory()

        cppConverter.saveToDir(outDir)

        val hppFile = File("$outDir/${getName()}.hpp")
        hppFile.createNewFile()
        hppFile.printWriter().use {
            it.print(hppConverter.convert())
            it.close()
        }

        enumConverter.saveToDir(outDir)

        if (!Path(outDir.toString(), getName() + "Config.hpp").exists())
            configConverter.saveToDir(outDir)
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
