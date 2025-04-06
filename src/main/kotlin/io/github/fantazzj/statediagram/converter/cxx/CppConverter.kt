package io.github.fantazzj.statediagram.converter.cxx

import io.github.fantazzj.statediagram.converter.Converter
import io.github.fantazzj.statediagram.structure.State
import java.io.File
import java.io.PrintWriter
import java.nio.file.Path

class CppConverter(name: String, states: Collection<State>) : Converter(name, states) {

    private val variables = CxxConverter.getVariables(states)

    private val objects = CxxConverter.getObjects(states)

    override fun saveToDir(outDir: Path) {
        val cppFile = File("$outDir/${getName()}.cpp")
        cppFile.createNewFile()
        addCppContent(cppFile)
    }

    private fun addCppContent(cppFile: File) =
        cppFile.printWriter().use { out ->
            writeInclude(out)
            out.println()
            //writeUsing(out)
            //out.println()
            writeConstructor(out)
            out.println()
            writeAutoCycle(out)
            out.println()
            writeOutputAnalysis(out)
            out.println()
            writeChangeState(out)
            out.close()
        }

    private fun writeInclude(out: PrintWriter) {
        out.println("#include \"${getName()}.hpp\"")
    }

    private fun writeUsing(out: PrintWriter) {
        out.println("using enum ${getName() + "State"};")
    }

    private fun writeOutputAnalysis(out: PrintWriter) {
        out.println("void ${getName()}::outputAnalysis() {")
        out.println("\toldState = newState;")
        out.println("\tswitch(newState) {")
        for (state in getStates()) {
            out.println("\t\tcase ${getName() + "State::" + state.getName()}:")
            for (action in state.getActions())
                out.println("\t\t\t${action.getAction()};")
            out.println("\t\t\tbreak;")
        }
        //out.println("\t\tdefault:")
        out.println("\t}")
        out.println("}")
    }

    private fun writeConstructor(out: PrintWriter) {
        out.print("${getName()}::${getName()}(")
        objects.forEach { o ->
            out.print("${getName() + "_" + o + "_t"} $o")
            if (o != objects.last())
                out.print(", ")
        }
        out.print(")")
        objects.forEach { o ->
            if (o == objects.first())
                out.print(" : ")
            out.print("$o($o)")
            if (o != objects.last())
                out.print(", ")
        }
        out.println("{")
        out.println("\tthis->newState = ${getName() + "State::" + getFirstState().getName()};")
        out.println("\tthis->oldState = ${getName() + "State::" + getFirstState().getName()};")
        out.println("\tthis->elapsedMillis = 0;")
        out.println("\tthis->previousMillis = 0;")
        variables.forEach { v ->
            out.println("\tthis->$v = ${v.uppercase()};")
        }
        out.println("}")
    }

    private fun writeChangeState(out: PrintWriter) {
        out.println("void ${getName()}::changeState(${getName() + "State"} newState) {")
        out.println("\tthis->newState = newState;")
        out.println("\telapsedMillis = 0;")
        out.println("\tpreviousMillis = MILLISECONDS;")
        out.println("}")
    }

    private fun writeAutoCycle(out: PrintWriter) {
        out.println("void ${getName()}::autoCycle() {")
        out.println("\telapsedMillis = MILLISECONDS - previousMillis;")
        out.println("\tswitch(newState) {")
        for (state in getStates()) {
            out.println("\t\tcase ${getName() + "State::" + state.getName()}:")
            if (state.getTransitions().isEmpty())
                out.println("\t\t\tbreak;")
            else for (transition in state.getTransitions()) {
                if (transition.getCondition() == "true") {
                    out.println("\t\t\tchangeState(${getName() + "State::" + transition.getTo()});")
                } else {
                    out.println("\t\t\tif(${transition.getCondition()}) {")
                    out.println("\t\t\t\tchangeState(${getName() + "State::" + transition.getTo()});")
                    out.println("\t\t\t\tbreak;")
                    out.println("\t\t\t}")
                }
            }
            out.println("\t\t\tbreak;")
        }
        out.println("\t}")
        out.println("}")
    }

}
