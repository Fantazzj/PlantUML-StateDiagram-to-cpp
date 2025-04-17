# PlantUML-StateDiagram-to-cpp

## What is it

This software aims to make easier to create software based on Moore's State Diagrams.
You can create a diagram in [PlantUML](https://plantuml.com), a really good software to design [State Diagrams](https://plantuml.com/state-diagram) and much more.
In PlantUML you can only get an image from your file, so I created this software to create a C++ implementation of your State Diagram.
When I code something using State Diagrams is usually something for a sort of automatic machine, which is usually done in [SFC](https://en.wikipedia.org/wiki/Sequential_function_chart), so the generated code is intended to be suitable for that goal.

## How it works

The software uses the official [PlantUML.jar](https://plantuml.com/download) to parse the PlantUML, then, with the acquired informations, it creates the C++ implementation.

## How to use it

If you're on Windows you can download the Windows version, there you can find a `plantuml-conv.exe`, you can simply drag the PlantUML file on it and it will create a folder named as your original file, it contains the whole C++ implementation
You can also use the command line:
```text
Usage: PlantUML-StateMachine-to-cpp [<options>] <inputfile>

Options:
  -v, --verbose        print all information
  --image              create also a png image of the diagram
  -o, --output=<path>  path to output folder
  -h, --help           Show this message and exit

Arguments:
  <inputfile>  input PlantUML file (needs correct extension)
```

## Important notes about the C++ implementation

- The output code is intended to be run on microcontrollers, so the implementations uses a very low effort structure
- The output code is divided in four files:
    1. StateDiagramName.hpp
    2. StateDiagramName.cpp
    3. StateDiagramNameEnum.hpp
    4. StateDiagramNameConfig.hpp
- Everything that starts with a capital letter it is intended to be a constant or an enum entry, so you're are supposed to declare it outside of the State Diagram's class

## TODO

- use iterator element `it` of Kotlin

## Credits

- [Convert icons created by Freepik - Flaticon](https://www.flaticon.com/free-icons/convert) for the icon used in the executable for the Windows version
- [PlantUML](https://plantuml.com) for the great software
- [Clikt](https://github.com/ajalt/clikt) fot the Kotlin library to create command line interfaces
