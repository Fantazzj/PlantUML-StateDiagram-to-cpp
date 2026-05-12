package io.github.fantazzj.statediagram.converter.cxx

import io.github.fantazzj.statediagram.converter.Assembler
import io.github.fantazzj.statediagram.converter.Converter
import io.github.fantazzj.statediagram.structure.Diagram

object ConfigConverter : Converter {

    override fun convert(diagram: Diagram): String {
        val variables = CxxConverter.getVariables(diagram.states)
        val objects = CxxConverter.getObjects(diagram.states)

        val out = getConfigContent(diagram, variables, objects)
        return out.toString()
    }

    private fun getConfigContent(diagram: Diagram, variables: Collection<String>, objects: Collection<String>): StringBuilder {
        return StringBuilder().run {
            also(openIncludeGuards(diagram))
            appendLine()
            also(defineVariablesTypes(diagram, variables, objects))
            appendLine()
            also(defineVariablesInitialValue(diagram, variables))
            appendLine()
            also(defineAdditionalAttributes(diagram))
            appendLine()
            also(closeIncludeGuards(diagram))
        }
    }

    private fun openIncludeGuards(diagram: Diagram): Assembler {
        return { out ->
            out.run {
                appendLine("#ifndef ${diagram.name.uppercase()}_CONFIG_HPP")
                appendLine("#define ${diagram.name.uppercase()}_CONFIG_HPP")
            }
        }
    }

    private fun defineVariablesTypes(diagram: Diagram, variables: Collection<String>, objects: Collection<String>): Assembler {
        return { out ->
            out.appendLine(
                (variables + objects)
                    .map { "typedef int ${diagram.name}_${it}_t;" }
                    .joinToString("\n")
            )
        }
    }

    private fun defineVariablesInitialValue(diagram: Diagram, variables: Collection<String>): Assembler {
        return { out ->
            out.appendLine(
                variables
                    .map { "typedef int ${diagram.name}_${it}_t;" }
                    .joinToString("\n")
            )
        }
    }

    private fun defineAdditionalAttributes(diagram: Diagram): Assembler {
        return { out ->
            out.run {
                appendLine("//if are unused can be safely deleted these two lines")
                appendLine("#define ${diagram.name.uppercase()}_ADDITIONAL_PRIVATE_ATT void* foo_priv")
                appendLine("#define ${diagram.name.uppercase()}_ADDITIONAL_PUBLIC_ATT void* foo_public")
            }
        }
    }

    private fun closeIncludeGuards(diagram: Diagram): Assembler {
        return { out ->
            out.appendLine("#endif //${diagram.name.uppercase()}_CONFIG_HPP")
        }
    }

}
