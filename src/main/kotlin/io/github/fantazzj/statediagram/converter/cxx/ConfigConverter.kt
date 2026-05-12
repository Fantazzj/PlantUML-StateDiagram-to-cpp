package io.github.fantazzj.statediagram.converter.cxx

import io.github.fantazzj.statediagram.converter.CodeAssembler
import io.github.fantazzj.statediagram.converter.Converter
import io.github.fantazzj.statediagram.structure.Diagram

object ConfigConverter : Converter {

    override fun convert(diagram: Diagram): String {
        val variables = CxxConverter.getVariables(diagram.states)
        val objects = CxxConverter.getObjects(diagram.states)

        val addCode = getConverter(diagram, variables, objects)

        return addCode("")
    }

    private fun getConverter(diagram: Diagram, variables: Collection<String>, objects: Collection<String>): CodeAssembler {
        val assemblers = listOf(
            openIncludeGuards(diagram),
            defineVariablesTypes(diagram, variables, objects),
            defineVariablesInitialValue(diagram, variables),
            defineAdditionalAttributes(diagram),
            closeIncludeGuards(diagram),
        )

        val converter = assemblers.reduce { f1, f2 -> { s -> f2(f1(s) + "\n") } }

        return converter
    }

    private val newLine: CodeAssembler = { s -> s + "\n" }

    private fun openIncludeGuards(diagram: Diagram): CodeAssembler {
        return { s ->
            s + listOf(
                "#ifndef ${diagram.name.uppercase()}_CONFIG_HPP",
                "#define ${diagram.name.uppercase()}_CONFIG_HPP",
            ).joinToString("\n") + "\n"
        }
    }

    private fun defineVariablesTypes(diagram: Diagram, variables: Collection<String>, objects: Collection<String>): CodeAssembler {
        return { s ->
            s + (variables + objects)
                .map { "typedef int ${diagram.name}_${it}_t;" }
                .joinToString("\n") + "\n"
        }
    }

    private fun defineVariablesInitialValue(diagram: Diagram, variables: Collection<String>): CodeAssembler {
        return { s ->
            s + variables
                .map { "typedef int ${diagram.name}_${it}_t;" }
                .joinToString("\n") + "\n"
        }
    }

    private fun defineAdditionalAttributes(diagram: Diagram): CodeAssembler {
        return { s ->
            s + listOf(
                "//if unused can be safely deleted",
                "#define ${diagram.name.uppercase()}_ADDITIONAL_PRIVATE_ATT void* foo_priv",
                "#define ${diagram.name.uppercase()}_ADDITIONAL_PUBLIC_ATT void* foo_public",
            ).joinToString("\n") + "\n"
        }
    }

    private fun closeIncludeGuards(diagram: Diagram): CodeAssembler {
        return { s ->
            s + "#endif //${diagram.name.uppercase()}_CONFIG_HPP" + "\n"
        }
    }

}
