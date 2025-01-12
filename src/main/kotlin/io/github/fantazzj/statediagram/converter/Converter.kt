package io.github.fantazzj.statediagram.converter

import java.nio.file.Path

interface Converter {

    fun saveToDir(outDir: Path)

}
