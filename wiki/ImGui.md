# ImGui

GroovyEngine integrates [Dear ImGui](https://github.com/ocornut/imgui), a powerful and easy-to-use graphical user interface library. This allows you to create custom in-game UIs for debugging, development, and more.

For general information about ImGui, please refer to the official [ImGui Wiki](https://github.com/ocornut/imgui/wiki).

For GroovyEngine-specific ImGui documentation, see the [ImGe Javadoc](../html/classio_1_1github_1_1luckymcdev_1_1groovyengine_1_1core_1_1client_1_1imgui_1_1_im_ge.html).

## Accessing ImGui

- **Toggle ImGui:** Press `F6` to show or hide the ImGui interface.
- **Toggle Input:** Press `G` to open a custom screen that captures all input, allowing you to interact with ImGui windows without affecting the game.

## Creating Custom Windows

To create a custom ImGui window, you must create a class that extends the `EditorWindow` class. You can see an example of this in the [DemoWindows](../html/classio_1_1github_1_1luckymcdev_1_1groovyengine_1_1core_1_1client_1_1editor_1_1windows_1_1_demo_windows.html) and its subclasses.

```java
public class MyCustomWindow extends EditorWindow {
    @Override
    public void draw() {
        ImGui.text("Hello, world!");
    }
}
```

Once you have created your window class, you must register it with the `WindowManager` in a custom module.

```java
public static void registerWindows() {
    WindowManager.registerWindow(new MyCustomWindow(), "My Category");
}
```

For more information on creating custom modules, see the [Modules](modules/Modules.md) page.

## ImGraphics

`ImGraphics` is a utility class that simplifies some of the more complex aspects of working with ImGui. We highly recommend reading through the Javadoc to learn more about what it can do.

Credit for the inspiration for this system goes to [LatvianModder](https://github.com/LatvianModder).
