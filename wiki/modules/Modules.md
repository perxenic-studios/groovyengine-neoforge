# Modules

## What are modules?
Modules are a way to organize your code. 
They allow you to create a custom basically entrypoints to the event bus and other main events.
See [Module](../html/interfaceio_1_1github_1_1luckymcdev_1_1groovyengine_1_1core_1_1systems_1_1module_1_1_module.html)

## How to use


To use Modules, you have to create a class, which implements the base
[Module](../html/interfaceio_1_1github_1_1luckymcdev_1_1groovyengine_1_1core_1_1systems_1_1module_1_1_module.html)
file, and register it via the [ModuleManager](../html/classio_1_1github_1_1luckymcdev_1_1groovyengine_1_1core_1_1systems_1_1module_1_1_module_manager.html).
For an example of a module look at: [CoreModule](../html/classio_1_1github_1_1luckymcdev_1_1groovyengine_1_1core_1_1_core_module.html)

Here is a quick example:

```java
import io.github.luckymcdev.groovyengine.core.systems.module.Module;

public class Foo implements Module {
    @Override
    public void init(IEventBus modEventBus) {
        System.out.println("This is a logLine on the init");
    }
}

```
> ![NOTE](https://img.shields.io/badge/NOTE-blue?style=for-the-badge)
> 
> Note: You shouldn't use System.out.println for logging, but instead use the classic modding logging tool
> SLF4J. See the [SLF4J User Manual](https://www.slf4j.org/manual.html)