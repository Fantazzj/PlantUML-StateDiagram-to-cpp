package io.github.fantazzj

import com.github.ajalt.clikt.core.*
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.check
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.path
import io.github.fantazzj.statediagram.converter.Converter
import io.github.fantazzj.statediagram.converter.cxx.CxxConverter
import io.github.fantazzj.statediagram.structure.Action
import io.github.fantazzj.statediagram.structure.Diagram
import io.github.fantazzj.statediagram.structure.State
import io.github.fantazzj.statediagram.structure.Transition
import net.sourceforge.plantuml.Previous
import net.sourceforge.plantuml.Run as PlantUmlJar
import net.sourceforge.plantuml.abel.Entity
import net.sourceforge.plantuml.abel.Link
import net.sourceforge.plantuml.core.UmlSource
import net.sourceforge.plantuml.preproc.PreprocessingArtifact
import net.sourceforge.plantuml.preproc.ReadLineList
import net.sourceforge.plantuml.preproc2.ReadFilterQuoteComment
import net.sourceforge.plantuml.statediagram.StateDiagram
import net.sourceforge.plantuml.statediagram.StateDiagramFactory
import net.sourceforge.plantuml.text.StringLocated
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

        val rl = ReadFilterQuoteComment().applyFilter(ReadLineList(inputFile.readLines(), null))

        val source = ArrayList<StringLocated>()
        do {
            val line = rl.readLine() ?: break
            if (line.string.isNotBlank())
                source.add(line)
        } while (true)
        rl.close()

        return source
    }

    private fun parsePlantUmlSource(source: List<StringLocated>): StateDiagram {
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

    private fun convertPlantUmlDiagram(diagram: StateDiagram): Collection<State> {
        val leafs = diagram.currentGroup.leafs() +
                diagram.groups().flatMap { it.leafs() }

        val links = diagram.links.map { if (it.isInverted) it.inv else it }

        if (verbose)
            plantUmlLog(links, leafs)

        val states = leafs.map {
            val name = it.name

            val actions = it.bodier.rawBody.map { Action(it.toString()) }

            val transitions = links
                .filter { it.entity1.name == name }
                .map {
                    Transition(
                        to = it.entity2.name,
                        condition = if (it.label.size() > 0) it.label.get(0).toString() else "true"
                    )
                }

            State(name, transitions, actions)
        }

        return states
    }

    private fun assembleDiagram(states: Collection<State>): Diagram {
        val name = inputFile.name.replace(extensionRegex, "")
        return Diagram(name, states)
    }

    override fun run() {
        val diagram = inputFile
            .let(::readFile)
            .let(::parsePlantUmlSource)
            .let(::convertPlantUmlDiagram)
            .let(::assembleDiagram)

        if (verbose) {
            println("Converting ${diagram.name}")
            println("States in converter's view:")
            diagram.states.forEach(::println)
            println("Whole diagram")
            println(diagram)
        }

        val outputDir =
            nullableOutputDir ?: Paths.get(inputFile.absolutePath.replace(extensionRegex, ""))

        if (verbose)
            println("Converted files will be saved in: \"$outputDir\"")

        CxxConverter.saveToDir(diagram, outputDir)

        if (outputImage)
            PlantUmlJar.main(arrayOf(inputFile.absolutePath, "-o", outputDir.toString()))
    }

}

fun main(args: Array<String>) = Main().main(args)
