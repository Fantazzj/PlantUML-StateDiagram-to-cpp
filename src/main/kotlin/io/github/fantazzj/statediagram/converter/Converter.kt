package io.github.fantazzj.statediagram.converter

import io.github.fantazzj.statediagram.structure.State
import java.nio.file.Path

abstract class Converter(private val name: String, private val states: Collection<State>) {

    fun getName() = name

    fun getStates() = states

    abstract fun saveToDir(outDir: Path)

}
