package io.github.fantazzj.statemachine.structure

class Transition(private val to: String, private val condition: String) {

    override fun toString(): String {
        return "--($condition)-> $to"
    }

    fun getTo(): String {
        return to
    }

    fun getCondition(): String {
        return condition
    }

}
