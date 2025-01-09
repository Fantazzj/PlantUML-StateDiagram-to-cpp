package io.github.fantazzj

import net.sourceforge.plantuml.core.UmlSource
import net.sourceforge.plantuml.statediagram.StateDiagram
import net.sourceforge.plantuml.statediagram.StateDiagramFactory
import net.sourceforge.plantuml.text.StringLocated
import java.io.File

fun main(args: Array<String>) {
    val source = ArrayList<StringLocated>()
    File(args[0]).forEachLine { line ->
        source.add(StringLocated(line, null))
    }

    val umlSource = UmlSource.create(source, false)
    val diagram = StateDiagramFactory().createSystem(umlSource, HashMap<String, String>())
    if (diagram !is StateDiagram)
        throw Exception("Given PlantUML is not a StateDiagram")

    println("Leafs")
    for (leaf in diagram.leafs())
        println(" - ${leaf.name}")
    val goodLinks = diagram.links.filter { l -> !l.isInverted }
    val invertedLinks = diagram.links.filter { l -> l.isInverted }.map { l -> l.inv }
    val rightLinks = goodLinks + invertedLinks
    println("Links:")
    for (link in rightLinks)
        println(" - ${link.entity1.name} -> ${link.entity2.name}")
    return
}
