package io.github.fantazzj.statediagram.converter.cxx

import io.github.fantazzj.statediagram.converter.Converter
import io.github.fantazzj.statediagram.structure.State
import java.io.File
import java.nio.file.Path

class EnumConverter(name: String, states: Collection<State>) : Converter(name, states) {

    override fun saveToDir(outDir: Path) {
        val enumFile = File("$outDir/${getName()}" + "State.hpp")
        enumFile.createNewFile()
        addEnumContent(enumFile)
    }

    private fun addEnumContent(enumFile: File) =
        enumFile.printWriter().use { out ->
            out.println("#ifndef ${getName().uppercase()}_ENUM_HPP")
            out.println("#define ${getName().uppercase()}_ENUM_HPP")
            out.println()
            out.println("enum class ${getName() + "State"} : int {")
            for (state in getStates())
                out.println("\t${state.getName()},")
            out.println("};")
            out.println()
            out.println("#endif //${getName().uppercase()}_ENUM_HPP")
        }

}
