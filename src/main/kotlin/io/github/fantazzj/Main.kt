package io.github.fantazzj

import net.sourceforge.plantuml.core.UmlSource
import net.sourceforge.plantuml.statediagram.StateDiagram
import net.sourceforge.plantuml.statediagram.StateDiagramFactory
import net.sourceforge.plantuml.text.StringLocated
import java.io.File

fun main(args: Array<String>) {
    val verbose = true

    val source = ArrayList<StringLocated>()
    File(args[0]).forEachLine { l ->
        source.add(StringLocated(l, null))
    }

    val umlSource = UmlSource.create(source, false)
    val diagram = StateDiagramFactory().createSystem(umlSource, HashMap<String, String>())
    if (diagram !is StateDiagram)
        throw Exception("Given PlantUML is not a StateDiagram")

    val leafs = diagram.leafs()
    if (verbose) {
        println("States:")
        leafs.forEach { l ->
            println(" - ${l.name}")
            println(" - \t${l.bodier.rawBody}")
        }
    }

    val links = diagram.links.map { l ->
        if (l.isInverted) l.inv
        else l
    }
    if (verbose) {
        println("Transitions:")
        links.forEach { l ->
            println(" - ${l.entity1.name} --${l.label}-> ${l.entity2.name}")
        }
    }
}
