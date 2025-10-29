# Threads Module

The Threads module is the heart of the scripting engine in GroovyEngine. It is responsible for loading, compiling, and executing all Groovy scripts.

For a general overview of how to write scripts, please see the [Scripts](../../Scripts.md) page.

## For Developers

If you need to customize the scripting environment, the Threads module provides the necessary hooks to do so.

### Custom Bindings and Shell Modification

To add custom variables (bindings) to the script execution shell or to modify the shell for security purposes, you can listen for the `ScriptEvent`.

This event is fired by the `ScriptManager` just before a script is executed. By listening to this event, you can access the script's `Binding` and the `GroovyShell` instance, allowing you to inject your own variables or modify the environment.

For detailed information, please refer to the Javadoc for:
- [ScriptEvent](../../html/classio_1_1github_1_1luckymcdev_1_1groovyengine_1_1threads_1_1core_1_1scripting_1_1event_1_1_script_event.html)
- [ScriptManager](../../html/classio_1_1github_1_1luckymcdev_1_1groovyengine_1_1threads_1_1core_1_1scripting_1_1core_1_1_script_manager.html)
