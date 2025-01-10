package io.github.fantazzj.statemachine.structure

class Action(private val action: String) {

    override fun toString(): String {
        return action
    }

    fun getAction(): String {
        return action
    }

}
