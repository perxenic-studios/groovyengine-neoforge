

# Welcome

# Getting started with GroovyEngine

Groovyengine is a mod, which aims to turn Minecraft into a Game engine. It does this by adding 
Groovy scripting and a lot of utilities for Rendering, Editing the world and more.

It adds a custom folder to your .minecraft folder, called "groovyengine".
See more in [Folder Structure](FolderStructure)


> ![NOTE](https://img.shields.io/badge/NOTE-blue?style=for-the-badge)
>
> The Code is well Documented and for everything mentioned in this wiki, you should look at the source code and read through the javadoc!

# Features

## How do the features work?

They work via Modules. Each module contains a set of features. The modules are named after what they do.
Construct is a module that contains all the features for constructing and editing the world.
Lens is a module that contains all the features for Rendering.
Core is a module that contains all the core features of groovyengine, not really important for users.
Threads is a module, which contains all the core scripting part.

## Scripting

Groovyengine uses Groovy as its scripting language. Groovy is a dynamic language, which is 
very easy to learn and use. It is also very fast and has a lot of features. 
see [Groovy Documentation](https://groovy-lang.org/)
Most notably the [Syntax](https://groovy-lang.org/syntax.html) and [Operators](https://groovy-lang.org/operators.html)

For scripting there is also a few custom things added. Most of the things important for scripting are inside of 
the Threads module. More notably in the 
[Api](../docs/html/namespaceio_1_1github_1_1luckymcdev_1_1groovyengine_1_1threads_1_1api.html) package.

## Construct

> See [Construct](modules/Construct)

## Threads

> See [Threads](modules/Threads)

## Lens

> See [Lens](modules/Lens)


## Core

> See [Core](modules/Core)




