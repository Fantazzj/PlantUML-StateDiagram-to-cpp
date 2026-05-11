package io.github.fantazzj.statediagram.converter.cxx

import io.github.fantazzj.statediagram.converter.Converter
import io.github.fantazzj.statediagram.structure.State
import java.io.File
import java.io.PrintWriter
import java.nio.file.Path

class EnumConverter(name: String, states: Collection<State>) : Converter(name, states) {

    override fun saveToDir(outDir: Path) {
        val enumFile = File("$outDir/${getName()}State.hpp")
        enumFile.createNewFile()
        addEnumContent(enumFile)
    }

    private fun addEnumContent(enumFile: File) =
        enumFile.printWriter().use { out ->
            openIncludeGuards(out)
            out.println()
            writeEnum(out)
            out.println()
            closeIncludeGuards(out)
        }

    private fun openIncludeGuards(out: PrintWriter) {
        out.println("#ifndef ${getName().uppercase()}_ENUM_HPP")
        out.println("#define ${getName().uppercase()}_ENUM_HPP")
    }

    private fun writeEnum(out: PrintWriter) {
        out.println("enum class ${getName()}State : int {")
        getStates().forEach { out.println("\t${it.name},") }
        out.println("};")
    }

    private fun closeIncludeGuards(out: PrintWriter) {
        out.println("#endif //${getName().uppercase()}_ENUM_HPP")
    }

}
