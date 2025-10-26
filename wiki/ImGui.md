# ImGui

For general ImGui Documentation see [ImGui Documentation](https://github.com/ocornut/imgui/wiki).

For more Groovyengine ImGui related Documentation look at 
[ImGe](..html/classio_1_1github_1_1luckymcdev_1_1groovyengine_1_1core_1_1client_1_1imgui_1_1_im_ge.html).

## How to Access.
To access ImGui, press `F6`. This will toggle ImGui displaying.
To access the custom Screen which will stop all your input to the game press `G`.

## Windows

GroovyEngine has a Windows system for imgui.
Every Window the user wants to create is a class that extends 
[EditorWindow](../html/classio_1_1github_1_1luckymcdev_1_1groovyengine_1_1core_1_1client_1_1editor_1_1core_1_1window_1_1_editor_window.html).
An example of that would be 
[Demo Windows and its subclasses](../html/classio_1_1github_1_1luckymcdev_1_1groovyengine_1_1core_1_1client_1_1editor_1_1windows_1_1_demo_windows.html).

You have to register windows with the WindowManager.

```java
public static void registerWindows() {
    WindowManager.registerWindow(new YourClassExtendingEditorWindow(), "category");
}
```
This method will have to be called in a Module. see [Custom Modules](Modules.md) on how to create one.

## ImGraphics

[ImGraphics](../html/classio_1_1github_1_1luckymcdev_1_1groovyengine_1_1core_1_1client_1_1imgui_1_1styles_1_1_im_graphics.html)
is a custom tool which makes it easier to work with some of ImGuis more finicky stuff. As always, read through the javadoc.
Courtesy to [LatvianModder](https://github.com/LatvianModder) who inspired me to make this system.

## 