# Material System

The Material system is a wrapper around Minecraft's `RenderType` system, designed to provide a more flexible and intuitive way to define the rendering properties of objects.

While `RenderType` is powerful, it can be cumbersome to work with. The Material system simplifies this by providing a clean and expressive API for defining how objects are rendered.

## Creating a Material

A `Material` is essentially a collection of rendering states, such as the texture, shader, and blend mode. To create a new material, you can use the `Material.builder()` method.

```java
Material myMaterial = Material.builder()
    .texture(new ResourceLocation("my_mod:textures/my_texture.png"))
    .shader(MyShaders.MY_CUSTOM_SHADER)
    .pipelineState(MyPipelineStates.MY_PIPELINE_STATE)
    .build();
```

For a more detailed look at how to create and use materials, please see the `GeMaterials` class, which contains many examples.

This system is designed to be used in conjunction with the [PipelineState](PipelineState.md) system, which allows you to define reusable sets of rendering states.
