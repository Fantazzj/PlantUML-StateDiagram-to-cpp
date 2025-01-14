package io.github.fantazzj.statediagram.converter

import io.github.fantazzj.statediagram.structure.State
import java.nio.file.Path

abstract class Converter(private val name: String, states: Collection<State>) {

    private val states = states.filter { s -> !s.getName().contains("*") }

    private val firstState = states.first { s ->
        s.getName() == states.first { ss ->
            ss.getName() == "*start*"
        }.getTransitions().first().getTo()
    }

    fun getFirstState() = firstState

    fun getName() = name

    fun getStates() = states

    abstract fun saveToDir(outDir: Path)

}
