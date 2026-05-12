package io.github.fantazzj.statediagram.converter.cxx

import io.github.fantazzj.statediagram.converter.Converter
import io.github.fantazzj.statediagram.structure.Diagram

typealias Assembler = (StringBuilder) -> Unit

object ConfigConverter : Converter {

    override fun convert(diagram: Diagram): String {
        val variables = CxxConverter.getVariables(diagram.states)
        val objects = CxxConverter.getObjects(diagram.states)

        val out = getConfigContent(diagram, variables, objects)
        return out.toString()
    }

    private fun getConfigContent(diagram: Diagram, variables: Collection<String>, objects: Collection<String>): StringBuilder {
        return StringBuilder()
            .also(openIncludeGuards(diagram))
            .appendLine()
            .also(defineVariablesTypes(diagram, variables, objects))
            .appendLine()
            .also(defineVariablesInitialValue(diagram, variables))
            .appendLine()
            .also(defineAdditionalAttributes(diagram))
            .appendLine()
            .also(closeIncludeGuards(diagram))
    }

    private fun openIncludeGuards(diagram: Diagram): Assembler {
        return { out ->
            out.appendLine("#ifndef ${diagram.name.uppercase()}_CONFIG_HPP")
            out.appendLine("#define ${diagram.name.uppercase()}_CONFIG_HPP")
        }
    }

    private fun defineVariablesTypes(diagram: Diagram, variables: Collection<String>, objects: Collection<String>): Assembler {
        return { out ->
            variables.forEach {
                out.appendLine("typedef int ${diagram.name}_${it}_t;")
            }

            objects.forEach {
                out.appendLine("typedef int ${diagram.name}_${it}_t;")
            }
        }
    }

    private fun defineVariablesInitialValue(diagram: Diagram, variables: Collection<String>): Assembler {
        return { out ->
            variables.forEach {
                out.appendLine("#define ${diagram.name.uppercase()}_${it.uppercase()} 0")
            }
        }
    }

    private fun defineAdditionalAttributes(diagram: Diagram): Assembler {
        return { out ->
            out.appendLine("//if are unused can be safely deleted these two lines")
            out.appendLine("#define ${diagram.name.uppercase()}_ADDITIONAL_PRIVATE_ATT void* foo_priv")
            out.appendLine("#define ${diagram.name.uppercase()}_ADDITIONAL_PUBLIC_ATT void* foo_public")
        }
    }

    private fun closeIncludeGuards(diagram: Diagram): Assembler {
        return { out ->
            out.appendLine("#endif //${diagram.name.uppercase()}_CONFIG_HPP")
        }
    }

}
