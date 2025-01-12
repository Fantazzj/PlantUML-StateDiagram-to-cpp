package io.github.fantazzj.statediagram.structure

class Transition(private val to: String, private val condition: String) {

    override fun toString(): String {
        return "with: ($condition) goto: $to"
    }

    fun getTo(): String {
        return to
    }

    fun getCondition(): String {
        return condition
    }

}
