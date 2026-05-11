package io.github.fantazzj.statediagram.structure

data class Diagram(val name: String, val states: Collection<State>) {

    val firstState = states.first { it.name == states.first { it.name == "*start*" }.transitions.first().to }

}
