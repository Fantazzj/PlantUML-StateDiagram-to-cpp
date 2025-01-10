package io.github.fantazzj

import io.github.fantazzj.statemachine.converter.Converter
import io.github.fantazzj.statemachine.converter.CppConverter
import io.github.fantazzj.statemachine.structure.State
import net.sourceforge.plantuml.abel.Entity
import net.sourceforge.plantuml.abel.Link
import net.sourceforge.plantuml.core.UmlSource
import net.sourceforge.plantuml.statediagram.StateDiagram
import net.sourceforge.plantuml.statediagram.StateDiagramFactory
import net.sourceforge.plantuml.text.StringLocated
import java.io.File

fun readFile(fileName: String): ArrayList<StringLocated> {
    val source = ArrayList<StringLocated>()
    File(fileName).forEachLine { l ->
        source.add(StringLocated(l, null))
    }
    return source
}

fun plantUmlParse(source: ArrayList<StringLocated>): StateDiagram {
    val umlSource = UmlSource.create(source, false)
    val diagram = StateDiagramFactory().createSystem(umlSource, HashMap<String, String>())
    if (diagram !is StateDiagram)
        throw Exception("Given PlantUML is not a StateDiagram")
    return diagram
}

fun plantUmlLog(links: List<Link>, leafs: Collection<Entity>) {
    println("Parsed states by PlantUML:")
    leafs.forEach { l ->
        println(" - ${l.name}")
        println(" - \t${l.bodier.rawBody}")
    }
    println("Parsed transitions by PlantUML:")
    links.forEach { l ->
        println(" - ${l.entity1.name} --${l.label}-> ${l.entity2.name}")
    }
}

fun main(args: Array<String>) {
    val verbose = args.contains("--verbose") or args.contains("-v")

    val inputFile = args[0]
    if (verbose) println("Input file is \"$inputFile\"")
    if (!inputFile.contains(Regex("(\\.puml|\\.plantuml|\\.uml)")))
        throw Exception("Given file is not a plantuml file")

    val outputDir = args[0].replace(Regex("(\\.puml|\\.plantuml|\\.uml)"), "")
    if (verbose) println("Converted files will be saved in: \"$outputDir\"")

    val source = readFile(args[0])
    val diagram = plantUmlParse(source)

    val leafs = diagram.leafs()
    val links = diagram.links.map { l ->
        if (l.isInverted) l.inv
        else l
    }

    if (verbose) plantUmlLog(links, leafs)

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

    if (verbose) {
        println("States in converter's view:")
        states.forEach(::println)
    }

    val converter: Converter = CppConverter("easy", states)
    converter.saveToDir("D:/Progetti/plantuml-1.2024.8/diagram-test/easy")

}
