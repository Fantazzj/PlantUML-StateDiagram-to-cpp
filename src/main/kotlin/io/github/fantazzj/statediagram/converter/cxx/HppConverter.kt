package io.github.fantazzj.statediagram.converter.cxx

import io.github.fantazzj.statediagram.converter.CodeAssembler
import io.github.fantazzj.statediagram.converter.CodeConverter
import io.github.fantazzj.statediagram.converter.Converter
import io.github.fantazzj.statediagram.structure.Diagram

object HppConverter : Converter {

    override fun convert(diagram: Diagram): String {
        val variables = CxxConverter.getVariables(diagram.states)
        val objects = CxxConverter.getObjects(diagram.states)

        val converter = getConverter(diagram, variables, objects)

        return converter()
    }

    private fun getConverter(diagram: Diagram, variables: Collection<String>, objects: Collection<String>): CodeConverter {
        val assemblers = listOf(
            openIncludeGuards(diagram),
            includeFiles(diagram),
            openClass(diagram),
            writePublic(),
            writeConstructor(diagram, objects),
            writePublicMethods(diagram),
            writePublicVariables(diagram, variables),
            writeAdditionalPublicAttributes(diagram),
            writePrivate(),
            writePrivateMethods(diagram),
            writeGeneralPrivateAttributes(diagram),
            writePrivateObjects(diagram, objects),
            writeAdditionalPrivateAttributes(diagram),
            closeClass(),
            closeIncludeGuards(diagram),
        )

        val addCode = assemblers.reduce { f1, f2 -> { s -> f2(f1(s) + "\n") } }

        return { addCode("") }
    }

    private fun openIncludeGuards(diagram: Diagram): CodeAssembler {
        return { s ->
            s + listOf(
                "#ifndef ${diagram.name.uppercase()}_HPP",
                "#define ${diagram.name.uppercase()}_HPP",
            ).joinToString("\n") + "\n"
        }
    }

    private fun includeFiles(diagram: Diagram): CodeAssembler {
        return { s ->
            s + listOf(
                "#include \"${diagram.name}State.hpp\"",
                "#include \"${diagram.name}Config.hpp\"",
            ).joinToString("\n") + "\n"
        }
    }

    private fun openClass(diagram: Diagram): CodeAssembler {
        return { s -> s + "class ${diagram.name} {" + "\n" }
    }

    private fun writePublic(): CodeAssembler {
        return { s -> s + "public:" + "\n" }
    }

    private fun writeConstructor(diagram: Diagram, objects: Collection<String>): CodeAssembler {
        val args = (listOf("Timer& timer") + objects.map { "${diagram.name}_${it}_t $it" }).joinToString(", ")
        return { s ->
            s + "\texplicit ${diagram.name}(${args});" + "\n"
        }
    }

    private fun writePublicMethods(diagram: Diagram): CodeAssembler {
        return { s ->
            s + listOf(
                "\tvoid autoCycle();",
                "\tvoid outputAnalysis();",
                "\t${diagram.name}State newState;",
            ).joinToString("\n") + "\n"
        }
    }

    private fun writePublicVariables(diagram: Diagram, variables: Collection<String>): CodeAssembler {
        return { s ->
            s + variables.joinToString("\n") { "\t${diagram.name}_${it}_t $it;" } + "\n"
        }
    }

    private fun writeAdditionalPublicAttributes(diagram: Diagram): CodeAssembler {
        return { s ->
            s + listOf(
                "\t#ifdef ${diagram.name.uppercase()}_ADDITIONAL_PUBLIC_ATT",
                "\t${diagram.name.uppercase()}_ADDITIONAL_PUBLIC_ATT",
                "\t#endif",
            ).joinToString("\n") + "\n"
        }
    }

    private fun writePrivate(): CodeAssembler {
        return { s -> s + "private:" + "\n" }
    }

    private fun writePrivateMethods(diagram: Diagram): CodeAssembler {
        return { s ->
            s + "\tvoid changeState(${diagram.name}State step);" + "\n"
        }
    }

    private fun writeGeneralPrivateAttributes(diagram: Diagram): CodeAssembler {
        return { s ->
            s + listOf(
                "\t${diagram.name}State oldState;",
                "\tunsigned long previousMillis;",
                "\tunsigned long elapsedMillis;",
                "\tTimer& timer;",
            ).joinToString("\n") + "\n"
        }
    }

    private fun writePrivateObjects(diagram: Diagram, objects: Collection<String>): CodeAssembler {
        return { s ->
            s + objects.joinToString { "\t${diagram.name}_${it}_t $it;" } + "\n"
        }
    }

    private fun writeAdditionalPrivateAttributes(diagram: Diagram): CodeAssembler {
        return { s ->
            s + listOf(
                "\t#ifdef ${diagram.name.uppercase()}_ADDITIONAL_PRIVATE_ATT",
                "\t${diagram.name.uppercase()}_ADDITIONAL_PRIVATE_ATT",
                "\t#endif",
            ).joinToString("\n") + "\n"
        }
    }

    private fun closeClass(): CodeAssembler {
        return { s -> s + "};" + "\n" }
    }

    private fun closeIncludeGuards(diagram: Diagram): CodeAssembler {
        return { s ->
            s + "#endif//${diagram.name.uppercase()}_HPP" + "\n"
        }
    }

}
