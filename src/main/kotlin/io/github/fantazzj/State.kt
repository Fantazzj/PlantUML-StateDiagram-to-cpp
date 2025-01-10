package io.github.fantazzj

class State(private val name: String) {

    fun getName() : String {
        return name
    }

    private val transitions = ArrayList<Transition>()

    fun addTransition(transition: Transition) {
        transitions.add(transition)
    }

    fun addTransition(to: String, condition: String) {
        transitions.add(Transition(to, condition))
    }

    private val actions = ArrayList<Action>()

    fun addAction(action: Action) {
        actions.add(action)
    }

    fun addAction(action: String) {
        actions.add(Action(action))
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
