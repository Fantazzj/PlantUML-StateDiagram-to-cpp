package io.github.fantazzj.statemachine.structure

import java.util.HashSet

class State(private val name: String) {

    private val transitions = HashSet<Transition>()

    private val actions = HashSet<Action>()

    fun getName(): String {
        return name
    }

    fun getTransitions(): Set<Transition> {
        return transitions
    }

    fun addTransition(transition: Transition) {
        transitions.add(transition)
    }

    fun addTransition(to: String, condition: String) {
        addTransition(Transition(to, condition))
    }

    fun getActions(): Set<Action> {
        return actions
    }

    fun addAction(action: Action) {
        actions.add(action)
    }

    fun addAction(action: String) {
        addAction(Action(action))
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("$name:")
        actions.forEach { a -> sb.append("\n\tdo: $a") }
        transitions.forEach { t -> sb.append("\n\t$t") }
        return sb.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as State
        return name == other.name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

}
