package io.github.fantazzj.statediagram.converter

import io.github.fantazzj.statediagram.structure.Diagram

typealias Assembler = (StringBuilder) -> Unit

interface Converter {

    fun convert(diagram: Diagram): String

}
