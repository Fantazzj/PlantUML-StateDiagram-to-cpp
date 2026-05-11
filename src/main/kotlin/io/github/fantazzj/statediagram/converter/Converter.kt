package io.github.fantazzj.statediagram.converter

import io.github.fantazzj.statediagram.structure.Diagram

interface Converter {

    fun convert(diagram: Diagram): String

}
