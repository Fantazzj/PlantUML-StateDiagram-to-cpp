package io.github.fantazzj.statediagram.converter.cxx

import io.github.fantazzj.statediagram.converter.Converter
import io.github.fantazzj.statediagram.structure.State
import java.nio.file.Path
import kotlin.io.path.createDirectory
import kotlin.io.path.exists

class CxxConverter(name: String, states: Collection<State>) : Converter(name, states) {

    private val cppConverter = CppConverter(name, states)
    private val hppConverter = HppConverter(name, states)
    private val enumConverter = EnumConverter(name, states)

    override fun saveToDir(outDir: Path) {
        if (!outDir.exists())
            outDir.createDirectory()
        cppConverter.saveToDir(outDir)
        hppConverter.saveToDir(outDir)
        enumConverter.saveToDir(outDir)
    }

}
