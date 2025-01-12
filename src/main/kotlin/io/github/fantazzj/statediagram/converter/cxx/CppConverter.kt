package io.github.fantazzj.statediagram.converter.cxx

import io.github.fantazzj.statediagram.converter.Converter
import io.github.fantazzj.statediagram.structure.State
import java.io.File
import java.io.PrintWriter
import java.nio.file.Path
import kotlin.io.path.createDirectory
import kotlin.io.path.exists

class CppConverter(private val name: String, private val states: Collection<State>) : Converter {

    override fun saveToDir(outDir: Path) {
        val cppFile = File("$outDir/$name.cpp")
        cppFile.createNewFile()
        addCppContent(cppFile)
    }

    private fun addCppContent(cppFile: File) =
        cppFile.printWriter().use { out ->
            writeInclude(out)
            out.println()
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
        out.println("#include \"$name.hpp\"")
    }

    private fun writeOutputAnalysis(out: PrintWriter) {
        out.println("void $name::outputAnalysis() {")
        out.println("\toldState = newState;")
        out.println("\tswitch(newState) {")
        for (state in states) {
            out.println("\t\tcase ${state.getName()}:")
            for (action in state.getActions())
                out.println("\t\t\t${action.getAction()};")
            out.println("\t\t\tbreak;")
        }
        //out.println("\t\tdefault:")
        out.println("\t}")
        out.println("}")
    }

    private fun writeConstructor(out: PrintWriter) {
        out.println("$name::$name() {")
        out.println("\tthis->newState = {FIRST_STATE};")
        out.println("\tthis->elapsedMillis = 0;")
        out.println("}")
    }

    private fun writeChangeState(out: PrintWriter) {
        out.println("void $name::changeState(State newState) {{")
        out.println("\tthis->newState = newState;")
        out.println("\telapsedMillis = 0;")
        out.println("\tpreviousMillis = Timer::milliseconds();")
        out.println("}")
    }

    private fun writeAutoCycle(out: PrintWriter) {
        out.println("void $name::autoCycle() {")
        out.println("\telapsedMillis = Timer::milliseconds() - previousMillis;")
        out.println("\tif( false ) ;")
        for (state in states)
            for (transition in state.getTransitions())
                out.println("\telse if( (newState == ${state.getName()}) && (${transition.getCondition()}) ) changeState(${transition.getTo()});")
        out.println("}")
    }

}
