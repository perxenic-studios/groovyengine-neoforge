# Welcome to GroovyEngine

GroovyEngine is a Minecraft mod that transforms the game into a powerful game engine. It integrates the Groovy scripting language and provides a rich set of utilities for rendering, world manipulation, and more.

When you first run GroovyEngine, it creates a `groovyengine` folder inside your `.minecraft` directory. To learn more about the project structure, see the [Folder Structure](FolderStructure.md) page.

> ![NOTE](https://img.shields.io/badge/NOTE-blue?style=for-the-badge)
>
> The GroovyEngine source code is well-documented. For in-depth information on any of the features mentioned in this wiki, we encourage you to explore the source and read the Javadoc comments.

## Features

GroovyEngine's features are organized into modules, each designed for a specific purpose:

- **[Construct](modules/Construct.md):** Tools for building and editing the game world.
- **[Lens](modules/lens/Lens.md):** A powerful rendering toolkit.
- **[Threads](modules/Threads.md):** Core scripting functionalities.
- **[Core](modules/Core.md):** The foundational module of GroovyEngine, which is less relevant for general users.

## Scripting with Groovy

GroovyEngine uses [Groovy](https://groovy-lang.org/), a dynamic and easy-to-learn language that runs on the Java Virtual Machine (JVM). Its clean syntax and powerful features make it an excellent choice for scripting in Minecraft.

For more details, refer to the official Groovy documentation, particularly the pages on [Syntax](https://groovy-lang.org/syntax.html) and [Operators](https://groovy-lang.org/operators.html).

Most of the scripting-related APIs are located in the `io.github.luckymcdev.groovyengine.threads.api` package within the [Threads](modules/Threads.md) module.
