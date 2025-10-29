# PipelineState

`PipelineState` is a fundamental concept in the Lens rendering engine. It represents a collection of rendering states that define how an object is drawn to the screen. This includes settings such as:

- **Depth Testing:** Whether the object should be hidden by objects in front of it.
- **Transparency:** How the object should be blended with the objects behind it.
- **Culling:** Whether the back-faces of the object should be rendered.
- **And many more...**

By encapsulating these states into a single object, `PipelineState` allows you to easily reuse and manage rendering configurations.

## Usage

`PipelineState` is primarily used in conjunction with the [Material](Materials.md) system. When you create a material, you can assign it a `PipelineState` to define its rendering behavior.

```java
// Create a custom pipeline state
PipelineState myPipelineState = PipelineState.builder()
    .depthTest(true)
    .transparency(new Transparency("my_transparency", () -> {
        // Transparency setup
    }, () -> {
        // Transparency cleanup
    }))
    .build();

// Create a material with the custom pipeline state
Material myMaterial = Material.builder()
    .pipelineState(myPipelineState)
    .build();
```

For a complete list of all the available pipeline states and their options, please see the [PipelineState Javadoc](../html/namespaceio_1_1github_1_1luckymcdev_1_1groovyengine_1_1lens_1_1client_1_1rendering_1_1pipeline_1_1state.html).
