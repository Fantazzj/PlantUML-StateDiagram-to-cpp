package io.github.fantazzj.statediagram.structure

class Diagram(val name: String, allStates: Collection<State>) {

    val firstState = allStates.first { it.name == allStates.first { it.name == "*start*" }.transitions.first().to }

    val states = allStates.filterNot { it.name.contains("*") }

    override fun toString() = "Diagram(name=$name, states=$states)"

}
