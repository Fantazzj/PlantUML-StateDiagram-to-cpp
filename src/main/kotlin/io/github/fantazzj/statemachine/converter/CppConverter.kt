package io.github.fantazzj.statemachine.converter

import io.github.fantazzj.statemachine.structure.State
import java.io.File
import java.io.PrintWriter
import java.nio.file.Path
import kotlin.io.path.createDirectory
import kotlin.io.path.exists

class CppConverter(private val name: String, private val states: Collection<State>) : Converter {

    override fun saveToDir(outDir: Path) {
        if (!outDir.exists())
            outDir.createDirectory()
        val hppFile = File("$outDir/$name.hpp")
        val cppFile = File("$outDir/$name.cpp")
        val enumFile = File("$outDir/$name" + "Enum.hpp")
        hppFile.createNewFile()
        cppFile.createNewFile()
        enumFile.createNewFile()
        addCppContent(cppFile)
        addHppContent(hppFile)
        addEnumContent(enumFile)
    }

    private fun addEnumContent(enumFile: File) =
        enumFile.printWriter().use { out ->
            out.println("#ifndef ${name.uppercase()}_ENUM_HPP")
            out.println("#define ${name.uppercase()}_ENUM_HPP")
            out.println()
            out.println("enum State : int {")
            for (state in states)
                out.println("\t${state.getName()},")
            out.println("};")
            out.println()
            out.println("#endif //${name.uppercase()}_ENUM_HPP\n")
            out.println()
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

    private fun addHppContent(hppFile: File) =
        hppFile.printWriter().use { out ->
            includeGuardsTop(out)
            out.println()
            includeFiles(out)
            out.println()
            out.println("class $name {")
            out.println("public:")
            publicMethods(out)
            publicAttributes(out)
            out.println()
            out.println("private:")
            privateMethods(out)
            privateAttributes(out)
            out.println("};")
            out.println()
            includeGuardsBottom(out)
            out.close()
        }

    private fun includeGuardsTop(out: PrintWriter) {
        out.println("#ifndef ${name.uppercase()}_HPP")
        out.println("#define ${name.uppercase()}_HPP")
    }

    private fun includeFiles(out: PrintWriter) {
        out.println("#include \"$name" + "Enum\".hpp")
        //out.println("#include \"../Timer/Timer.hpp\"")
    }

    private fun publicMethods(out: PrintWriter) {
        out.println("\t$name();")
        out.println("\tvoid autoCycle();")
        out.println("\tvoid outputAnalysis();")
        out.println("\tState newState;")
    }

    private fun publicAttributes(out: PrintWriter) {

    }

    private fun privateMethods(out: PrintWriter) {
        out.println("\tvoid changeState(State step);")
    }

    private fun privateAttributes(out: PrintWriter) {
        out.println("\tState oldState;")
        out.println("\tunsigned long previousMillis;")
        out.println("\tunsigned long elapsedMillis;")
    }

    private fun includeGuardsBottom(out: PrintWriter) {
        out.println("#endif//${name.uppercase()}_HPP")
    }

}
