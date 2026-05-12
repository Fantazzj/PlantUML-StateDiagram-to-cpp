package io.github.fantazzj.statediagram.converter

import io.github.fantazzj.statediagram.structure.Diagram

typealias CodeAssembler = (String) -> String

interface Converter {

    fun convert(diagram: Diagram): String

}
