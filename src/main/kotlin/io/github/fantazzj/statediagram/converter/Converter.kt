package io.github.fantazzj.statediagram.converter

import io.github.fantazzj.statediagram.structure.Diagram
import java.nio.file.Path
import kotlin.system.exitProcess

abstract class Converter(private val diagram: Diagram) {

    private val states = diagram.states.filter { s -> !s.name.contains("*") }

    private val firstState =
        try {
            diagram.states.first {
                it.name == diagram.states.first { it.name == "*start*" }.transitions.first().to
            }
        } catch (_: NoSuchElementException) {
            println("Error")
            println("Missing initial state")
            exitProcess(1)
        }

    fun getFirstState() = firstState

    fun getName() = diagram.name

    fun getStates() = states

    abstract fun saveToDir(outDir: Path)

}
