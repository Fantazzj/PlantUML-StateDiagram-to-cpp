package io.github.fantazzj.statediagram.converter

import io.github.fantazzj.statediagram.structure.State
import java.nio.file.Path
import kotlin.system.exitProcess

abstract class Converter(private val name: String, states: Collection<State>) {

    private val states = states.filter { s -> !s.getName().contains("*") }

    private val firstState =
        try {
            states.first { s ->
                s.getName() == states.first { ss ->
                    ss.getName() == "*start*"
                }.getTransitions().first().to
            }
        } catch (_: NoSuchElementException) {
            println("Error")
            println("Missing initial state")
            exitProcess(1)
        }

    fun getFirstState() = firstState

    fun getName() = name

    fun getStates() = states

    abstract fun saveToDir(outDir: Path)

}
