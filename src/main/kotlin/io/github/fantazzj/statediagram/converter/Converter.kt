package io.github.fantazzj.statediagram.converter

import io.github.fantazzj.statediagram.structure.Diagram
import java.nio.file.Path

abstract class Converter(private val diagram: Diagram) {

    private val states = diagram.states

    fun getFirstState() = diagram.firstState

    fun getName() = diagram.name

    fun getStates() = states

    abstract fun saveToDir(outDir: Path)

}
