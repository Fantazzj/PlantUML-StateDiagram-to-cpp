package io.github.fantazzj.statediagram.converter.cxx

import io.github.fantazzj.statediagram.converter.CodeAssembler
import io.github.fantazzj.statediagram.converter.CodeConverter
import io.github.fantazzj.statediagram.converter.Converter
import io.github.fantazzj.statediagram.structure.Diagram

object CppConverter : Converter {

    override fun convert(diagram: Diagram): String {
        val variables = CxxConverter.getVariables(diagram.states)
        val objects = CxxConverter.getObjects(diagram.states)

        val converter = getConverter(diagram, variables, objects)

        return converter()
    }

    private fun getConverter(diagram: Diagram, variables: Collection<String>, objects: Collection<String>): CodeConverter {
        val assemblers = listOf(
            writeInclude(diagram),
            writeConstructor(diagram, variables, objects),
            writeAutoCycle(diagram),
            writeOutputAnalysis(diagram),
            writeChangeState(diagram),
        )

        val addCode = assemblers.reduce { f1, f2 -> { s -> f2(f1(s) + "\n") } }

        return { addCode("") }
    }

    private fun writeInclude(diagram: Diagram): CodeAssembler {
        return { s ->
            s + "#include \"${diagram.name}.hpp\"" + "\n"
        }
    }

    private fun writeOutputAnalysis(diagram: Diagram): CodeAssembler {
        val switchContent = diagram.states.joinToString("\n") {
            "\t\tcase ${diagram.name}State::${it.name}:" + "\n" +
                    it.actions.joinToString("\n") { "\t\t\t${it.action};" } + "\n" +
                    "\t\t\tbreak;"
        }

        return { s ->
            s + listOf(
                "void ${diagram.name}::outputAnalysis() {",
                "\toldState = newState;",
                "\tswitch(newState) {",
                switchContent,
                "\t\tdefault:",
                "\t}",
                "}",
            ).joinToString("\n") + "\n"
        }
    }

    private fun writeConstructor(diagram: Diagram, variables: Collection<String>, objects: Collection<String>): CodeAssembler {
        val args = (listOf("Timer& timer") + objects.map { "${diagram.name}_${it}_t $it" }).joinToString(", ")
        val initObjects = (listOf("timer(timer)") + objects.map { "$it($it)" }).joinToString(", ")
        val initVariables = variables.joinToString("\n") { "\tthis->$it = ${diagram.name.uppercase()}_${it.uppercase()};" }

        return { s ->
            s + listOf(
                "${diagram.name}::${diagram.name}($args) : $initObjects {",
                "\tthis->newState = ${diagram.name}State::${diagram.firstState.name};",
                "\tthis->oldState = ${diagram.name}State::${diagram.firstState.name};",
                "\tthis->elapsedMillis = 0;",
                "\tthis->previousMillis = 0;",
                initVariables,
                "}",
            ).joinToString("\n") + "\n"
        }
    }

    private fun writeChangeState(diagram: Diagram): CodeAssembler {
        return { s ->
            s + listOf(
                "void ${diagram.name}::changeState(${diagram.name}State newState) {",
                "\tthis->newState = newState;",
                "\telapsedMillis = 0;",
                "\tpreviousMillis = timer.millis();",
                "}",
            ).joinToString("\n") + "\n"
        }
    }

    private fun writeAutoCycle(diagram: Diagram): CodeAssembler {
        val switchContent = diagram.states.joinToString("\n") {
            "\t\tcase ${diagram.name}State::${it.name}:" + "\n" +
                    it.transitions.joinToString("\n") {
                        if (it.condition == "true") "\t\t\tchangeState(${diagram.name}State::${it.to});"
                        else listOf(
                            "\t\t\tif(${it.condition}) {",
                            "\t\t\t\tchangeState(${diagram.name}State::${it.to});",
                            "\t\t\t\tbreak;",
                            "\t\t\t}",
                        ).joinToString("\n")
                    } + "\n" +
                    "\t\t\tbreak;"
        }

        return { s ->
            s + listOf(
                "void ${diagram.name}::autoCycle() {",
                "\telapsedMillis = timer.millis() - previousMillis;",
                "\tswitch(newState) {",
                switchContent,
                "\t}",
                "}",
            ).joinToString("\n") + "\n"
        }
    }

}
