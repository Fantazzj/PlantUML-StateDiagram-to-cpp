package io.github.fantazzj.statemachine.converter

import java.nio.file.Path

interface Converter {

    fun saveToDir(outDir: Path)

}
