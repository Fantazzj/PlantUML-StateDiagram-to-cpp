package io.github.fantazzj

import com.github.ajalt.clikt.core.*
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.check
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.path
import io.github.fantazzj.statediagram.converter.Converter
import io.github.fantazzj.statediagram.converter.cxx.CxxConverter
import io.github.fantazzj.statediagram.structure.State
import net.sourceforge.plantuml.Previous
import net.sourceforge.plantuml.Run
import net.sourceforge.plantuml.abel.Entity
import net.sourceforge.plantuml.abel.Link
import net.sourceforge.plantuml.core.UmlSource
import net.sourceforge.plantuml.preproc.PreprocessingArtifact
import net.sourceforge.plantuml.statediagram.StateDiagram
import net.sourceforge.plantuml.statediagram.StateDiagramFactory
import net.sourceforge.plantuml.text.StringLocated
import net.sourceforge.plantuml.text.TLineType
import java.io.File
import java.nio.file.Paths

class Main : CliktCommand(name = "PlantUML-StateMachine-to-cpp") {

    private val extensionRegex = Regex("(\\.puml|\\.plantuml|\\.uml)")

    private val inputFile by argument(help = "input PlantUML file (needs correct extension)").file(
        mustExist = true,
        canBeDir = false
    ).check { it.name.contains(extensionRegex) }
    private val verbose by option("-v", "--verbose", help = "print all information").flag()
    private val outputImage by option("--image", help = "create also a png image of the diagram").flag()
    private val nullableOutputDir by option("-o", "--output", help = "path to output folder").path()

    private fun readFile(inputFile: File): List<StringLocated> {
        if (verbose)
            println("Input file is \"$inputFile\"")

        val source = inputFile.readLines().map {
            StringLocated(it, null)
        }.filterNot {
            it.type == TLineType.COMMENT_SIMPLE || it.string.isBlank()
        }.map {
            it.removeInnerComment()
        }

        return source
    }

    private fun plantUmlParse(source: List<StringLocated>): StateDiagram {
        val umlSource = UmlSource.create(source, false)
        val diagram = StateDiagramFactory().createSystem(umlSource, Previous.createEmpty(), PreprocessingArtifact())
        if (diagram !is StateDiagram)
            throw Exception("Given PlantUML is not a StateDiagram")
        return diagram
    }

    private fun plantUmlLog(links: Collection<Link>, leafs: Collection<Entity>) {
        println("Parsed states by PlantUML:")
        leafs.forEach {
            println(" - ${it.name}")
            println(" - \t${it.bodier.rawBody}")
        }
        println("Parsed transitions by PlantUML:")
        links.forEach {
            println(" - ${it.entity1.name} --${it.label}-> ${it.entity2.name}")
        }
    }

    private fun plantUmlToMine(diagram: StateDiagram): Collection<State> {
        val leafs = diagram.currentGroup.leafs()
        val links = diagram.links.map {
            if (it.isInverted) it.inv
            else it
        }

        if (verbose)
            plantUmlLog(links, leafs)

        val states = ArrayList<State>()
        leafs.forEach { l ->
            val state = State(l.name)
            l.bodier.rawBody.forEach { b ->
                state.addAction(b.toString())
            }
            states.add(state)
        }
        links.forEach { l ->
            states.forEach { s ->
                if (l.entity1.name == s.getName())
                    s.addTransition(
                        l.entity2.name,
                        if (l.label.size() > 0) l.label.get(0).toString()
                        else "true"
                    )
            }
        }
        return states
    }

    override fun run() {
        val source = readFile(inputFile)

        val diagram = plantUmlParse(source)

        val states = plantUmlToMine(diagram)

        val diagramName = inputFile.name.replace(extensionRegex, "")
        if (verbose) {
            println("Converting $diagramName")
            println("States in converter's view:")
            states.forEach(::println)
        }

        val outputDir =
            nullableOutputDir ?: Paths.get(inputFile.absolutePath.replace(extensionRegex, ""))

        if (verbose)
            println("Converted files will be saved in: \"$outputDir\"")

        val converter: Converter = CxxConverter(diagramName, states)
        converter.saveToDir(outputDir)

        if (outputImage)
            Run.main(arrayOf(inputFile.absolutePath, "-o", outputDir.toString()))
    }

}

fun main(args: Array<String>) = Main().main(args)
