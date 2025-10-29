# Modules

## What Are Modules?

Modules are the core components for organizing your code and integrating with GroovyEngine. They act as the primary entry points for hooking into the engine's lifecycle, such as initialization, and for registering event listeners on the event bus.

By creating a custom module, you can:
- Register event handlers.
- Initialize your own systems.
- Register custom ImGui windows.
- And much more.

See the [Module interface Javadoc](../html/interfaceio_1_1github_1_1luckymcdev_1_1groovyengine_1_1core_1_1systems_1_1module_1_1_module.html) for the base specification.

## Creating and Registering a Module

To create a module, you need to write a class that implements the `Module` interface. You then register an instance of this class by listening for the `RegisterModuleEvent`.

Here’s a step-by-step guide:

### 1. Create the Module Class

Create a new Groovy script file. Inside, define a class that implements the `Module` interface. This interface requires you to implement several methods, such as `init()`, which is called during the engine's initialization phase.

**Example: `MyModule.groovy`**
```groovy
import io.github.luckymcdev.groovyengine.core.systems.module.Module
import net.neoforged.bus.api.IEventBus
import org.slf4j.Logger
import org.slf4j.LoggerFactory

// It's good practice to give modules a high priority to ensure they load before other scripts.
//priority=100 

class MyModule implements Module {
    public static final Logger LOGGER = LoggerFactory.getLogger("MyModule")

    @Override
    void init(IEventBus modEventBus) {
        LOGGER.info("MyModule is initializing!")
        // You can register event listeners here, e.g.:
        // modEventBus.addListener(this.&onSomeEvent)
    }
    
    // Other implemented methods from the Module interface would go here...
}
```
> **A Note on Logging:** It is strongly recommended to use a proper logging framework like SLF4J instead of `System.out.println`. This provides more control over log levels and output formatting. See the [SLF4J User Manual](https://www.slf4j.org/manual.html) for more details.

### 2. Register the Module

To make the engine aware of your module, you must listen for the `RegisterModuleEvent` and add your module to it. This is typically done in a separate script that contains the event handler.

**Example: `Registration.groovy`**
```groovy
//priority=100
import io.github.luckymcdev.groovyengine.core.systems.module.RegisterModuleEvent
import net.neoforged.bus.api.SubscribeEvent

@EventBusSubscriber
class ModuleRegistration {
    @SubscribeEvent
    void onRegisterModules(RegisterModuleEvent event) {
        event.register(new MyModule())
    }
}
```
By following this pattern, you can create modular and organized features for GroovyEngine. For a real-world example, see the [CoreModule source code](../html/classio_1_1github_1_1luckymcdev_1_1groovyengine_1_1core_1_1_core_module.html).
