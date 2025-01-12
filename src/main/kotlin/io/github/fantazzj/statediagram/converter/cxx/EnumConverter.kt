package io.github.fantazzj.statediagram.converter.cxx

import io.github.fantazzj.statediagram.converter.Converter
import io.github.fantazzj.statediagram.structure.State
import java.io.File
import java.nio.file.Path

class EnumConverter(private val name: String, private val states: Collection<State>) : Converter {

    override fun saveToDir(outDir: Path) {
        val enumFile = File("$outDir/$name" + "Enum.hpp")
        enumFile.createNewFile()
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
            out.println("#endif //${name.uppercase()}_ENUM_HPP")
        }

}
