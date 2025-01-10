package io.github.fantazzj

class Transition(private val to: String, private val condition: String) {

    override fun toString(): String {
        return "--($condition)-> $to"
    }

}
