package io.github.fantazzj.statediagram.converter.cxx

import io.github.fantazzj.statediagram.converter.Converter
import io.github.fantazzj.statediagram.structure.Diagram

class CppConverter(private val diagram: Diagram) : Converter {

    private val variables = CxxConverter.getVariables(diagram.states)

    private val objects = CxxConverter.getObjects(diagram.states)

    override fun convert(): String {
        val out = StringBuilder()
        addCppContent(out)
        return out.toString()
    }

    private fun addCppContent(out: StringBuilder) {
        out
            .also(::writeInclude)
            .appendLine()
            .also(::writeConstructor)
            .appendLine()
            .also(::writeAutoCycle)
            .appendLine()
            .also(::writeOutputAnalysis)
            .appendLine()
            .also(::writeChangeState)
    }

    private fun writeInclude(out: StringBuilder) {
        out.appendLine("#include \"${diagram.name}.hpp\"")
    }

    private fun writeOutputAnalysis(out: StringBuilder) {
        out.appendLine("void ${diagram.name}::outputAnalysis() {")
        out.appendLine("\toldState = newState;")
        out.appendLine("\tswitch(newState) {")
        for (state in diagram.states) {
            out.appendLine("\t\tcase ${diagram.name}State::${state.name}:")
            for (action in state.actions)
                out.appendLine("\t\t\t${action.action};")
            out.appendLine("\t\t\tbreak;")
        }
        //out.appendLine("\t\tdefault:")
        out.appendLine("\t}")
        out.appendLine("}")
    }

    private fun writeConstructor(out: StringBuilder) {
        out.append("${diagram.name}::${diagram.name}(Timer& timer")
        objects.forEach { o ->
            out.append(", ")
            out.append("${diagram.name}_${o}_t $o")
        }
        out.append(") : timer(timer) ")
        objects.forEach { o ->
            out.append(", ")
            out.append("$o($o)")
        }
        out.appendLine("{")
        out.appendLine("\tthis->newState = ${diagram.name}State::${diagram.firstState.name};")
        out.appendLine("\tthis->oldState = ${diagram.name}State::${diagram.firstState.name};")
        out.appendLine("\tthis->elapsedMillis = 0;")
        out.appendLine("\tthis->previousMillis = 0;")
        variables.forEach { v ->
            out.appendLine("\tthis->$v = ${diagram.name.uppercase()}_${v.uppercase()};")
        }
        out.appendLine("}")
    }

    private fun writeChangeState(out: StringBuilder) {
        out.appendLine("void ${diagram.name}::changeState(${diagram.name}State newState) {")
        out.appendLine("\tthis->newState = newState;")
        out.appendLine("\telapsedMillis = 0;")
        out.appendLine("\tpreviousMillis = timer.millis();")
        out.appendLine("}")
    }

    private fun writeAutoCycle(out: StringBuilder) {
        out.appendLine("void ${diagram.name}::autoCycle() {")
        out.appendLine("\telapsedMillis = timer.millis() - previousMillis;")
        out.appendLine("\tswitch(newState) {")
        for (state in diagram.states) {
            out.appendLine("\t\tcase ${diagram.name}State::${state.name}:")
            if (state.transitions.isEmpty())
                out.appendLine("\t\t\tbreak;")
            else for (transition in state.transitions) {
                if (transition.condition == "true") {
                    out.appendLine("\t\t\tchangeState(${diagram.name}State::${transition.to});")
                } else {
                    out.appendLine("\t\t\tif(${transition.condition}) {")
                    out.appendLine("\t\t\t\tchangeState(${diagram.name}State::${transition.to});")
                    out.appendLine("\t\t\t\tbreak;")
                    out.appendLine("\t\t\t}")
                }
            }
            out.appendLine("\t\t\tbreak;")
        }
        out.appendLine("\t}")
        out.appendLine("}")
    }

}
