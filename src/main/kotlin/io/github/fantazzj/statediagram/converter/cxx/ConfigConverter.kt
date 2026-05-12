package io.github.fantazzj.statediagram.converter.cxx

import io.github.fantazzj.statediagram.converter.CodeAssembler
import io.github.fantazzj.statediagram.converter.Converter
import io.github.fantazzj.statediagram.structure.Diagram

object ConfigConverter : Converter {

    override fun convert(diagram: Diagram): String {
        val variables = CxxConverter.getVariables(diagram.states)
        val objects = CxxConverter.getObjects(diagram.states)

        return getConfigContent(diagram, variables, objects)
    }

    private fun getConfigContent(diagram: Diagram, variables: Collection<String>, objects: Collection<String>): String {
        return ""
            .let(openIncludeGuards(diagram))
            .let(newLine())
            .let(defineVariablesTypes(diagram, variables, objects))
            .let(newLine())
            .let(defineVariablesInitialValue(diagram, variables))
            .let(newLine())
            .let(defineAdditionalAttributes(diagram))
            .let(newLine())
            .let(closeIncludeGuards(diagram))
    }

    private fun newLine(): CodeAssembler {
        return { s -> s + "\n" }
    }

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
                "//if are unused can be safely deleted these two lines",
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
