# RenderUtils

`RenderUtils` is a utility class that provides a collection of useful methods and constants for rendering. It is a grab-bag of useful functions that don't fit neatly into any other category.

Some of the things you can find in `RenderUtils` include:
- The full-bright lightmap value (`15728880`).
- Methods for drawing shapes, lines, and other primitives.
- And much more.

For a complete list of what `RenderUtils` has to offer, please see the [RenderUtils Javadoc](../html/classio_1_1github_1_1luckymcdev_1_1groovyengine_1_1lens_1_1client_1_1rendering_1_1util_1_1_render_utils.html).

## RenderTargetUtils

`RenderTargetUtils` is another utility class that provides a single, but very useful, method:

```java
public static RenderTarget copyRenderTarget(RenderTarget source, RenderTarget existing)
```

This method allows you to copy the contents of one `RenderTarget` to another. This is particularly useful for post-processing effects, where you may need to read from and write to the same render target in a single frame.

For more information, please see the [RenderTargetUtils Javadoc](../html/classio_1_1github_1_1luckymcdev_1_1groovyengine_1_1lens_1_1client_1_1rendering_1_1util_1_1_render_target_util.html).
