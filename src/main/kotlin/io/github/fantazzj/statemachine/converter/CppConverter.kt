package io.github.fantazzj.statemachine.converter

import io.github.fantazzj.statemachine.structure.State
import java.io.File
import java.nio.file.Paths
import kotlin.io.path.createDirectory
import kotlin.io.path.exists

class CppConverter(private val name: String, private val states: List<State>) : Converter {

    override fun saveToDir(dirName: String) {
        val path = Paths.get(dirName)
        if (!path.exists())
            path.createDirectory()
        val hppFile = File("$dirName/$name.hpp")
        val cppFile = File("$dirName/$name.cpp")
        hppFile.createNewFile()
        cppFile.createNewFile()
        addCppContent(cppFile)
        addHppContent(hppFile)
    }

    private fun addCppContent(cppFile: File) =
        cppFile.bufferedWriter().use { out ->
            out.write("AAAAA")
        }

    private fun addHppContent(hppFile: File) =
        hppFile.bufferedWriter().use { out ->
            out.write("AAAAA")
        }

}
