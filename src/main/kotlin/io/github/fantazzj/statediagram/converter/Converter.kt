package io.github.fantazzj.statediagram.converter

import io.github.fantazzj.statediagram.structure.Diagram

abstract class Converter(private val diagram: Diagram) {

    fun getFirstState() = diagram.firstState

    fun getName() = diagram.name

    fun getStates() = diagram.states

}
