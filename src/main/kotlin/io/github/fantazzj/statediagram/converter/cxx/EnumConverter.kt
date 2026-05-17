package io.github.fantazzj.statediagram.converter.cxx

import io.github.fantazzj.statediagram.converter.CodeAssembler
import io.github.fantazzj.statediagram.converter.CodeConverter
import io.github.fantazzj.statediagram.converter.Converter
import io.github.fantazzj.statediagram.structure.Diagram

object EnumConverter : Converter {

    override fun convert(diagram: Diagram): String {
        val converter = getConverter(diagram)

        return converter()
    }

    private fun getConverter(diagram: Diagram): CodeConverter {
        val assemblers = listOf(
            openIncludeGuards(diagram),
            openEnum(diagram),
            writeStates(diagram, "    "),
            closeEnum(),
            closeIncludeGuards(diagram),
        )

        val addCode = assemblers.reduce { f1, f2 -> { s -> f2(f1(s) + "\n") } }

        return { addCode("") }
    }

    private fun openIncludeGuards(diagram: Diagram): CodeAssembler {
        return { s ->
            s + listOf(
                "#ifndef ${diagram.name.uppercase()}_ENUM_HPP",
                "#define ${diagram.name.uppercase()}_ENUM_HPP",
            ).joinToString("\n") + "\n"
        }
    }

    private fun openEnum(diagram: Diagram): CodeAssembler {
        return { s ->
            s + "enum class ${diagram.name}State : int {" + "\n"
        }
    }

    private fun writeStates(diagram: Diagram, indentation: String): CodeAssembler {
        return { s ->
            s + diagram.states.joinToString("\n") { indentation + "${it.name}," } + "\n"
        }
    }

    private fun closeEnum(): CodeAssembler {
        return { s ->
            s + "};" + "\n"
        }
    }

    private fun closeIncludeGuards(diagram: Diagram): CodeAssembler {
        return { s ->
            s + "#endif //${diagram.name.uppercase()}_ENUM_HPP" + "\n"
        }
    }

}
