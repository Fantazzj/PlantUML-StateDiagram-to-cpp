package io.github.fantazzj

import io.github.fantazzj.statemachine.converter.Converter
import io.github.fantazzj.statemachine.converter.CppConverter
import io.github.fantazzj.statemachine.structure.State
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

fun main(args: Array<String>) {
    val verbose = args.contains("--verbose") or args.contains("-v")

    val source = readFile(args[0])
    val diagram = plantUmlParse(source)

    val leafs = diagram.leafs()
    val links = diagram.links.map { l ->
        if (l.isInverted) l.inv
        else l
    }
    if (verbose) {
        println("States:")
        leafs.forEach { l ->
            println(" - ${l.name}")
            println(" - \t${l.bodier.rawBody}")
        }
        println("Transitions:")
        links.forEach { l ->
            println(" - ${l.entity1.name} --${l.label}-> ${l.entity2.name}")
        }
    }

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

    if (verbose) states.forEach(::println)

    val converter: Converter = CppConverter("easy", states)
    converter.saveToDir("D:/Progetti/plantuml-1.2024.8/diagram-test/easy")

}
