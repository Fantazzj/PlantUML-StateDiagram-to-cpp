package io.github.fantazzj.statediagram.structure

data class State(val name: String, val transitions: Collection<Transition>, val actions: Collection<Action>)
