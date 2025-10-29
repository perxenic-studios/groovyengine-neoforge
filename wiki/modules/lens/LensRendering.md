# LensRendering and Post-Processing

`LensRendering` is a key class within the Lens module that manages the post-processing chain. This system allows you to apply full-screen shader effects to the game's final rendered image.

## The Post-Processing Chain

The post-processing chain is a sequence of shader effects that are applied one after another. Each effect in the chain takes the output of the previous effect as its input, allowing you to create complex and layered visual effects.

### Adding an Effect

To add a new effect to the post-processing chain, you must create an object that extends the `PostProcessChain.PostProcessEffect` abstract class and then register it with the `LensRendering.POST_CHAIN`.

You can do this by creating a separate class file, or by using an anonymous inner class for simpler effects.

#### Example: Using an Anonymous Inner Class

```java
public static void registerPostProcessingEffects() {
    // Create a new effect using an anonymous inner class
    PostProcessChain.PostProcessEffect myEffect = new PostProcessChain.PostProcessEffect() {
        @Override
        public void apply() {
            // Bind your shader
            myCustomShader.bind();
            
            // Set any uniforms
            myCustomShader.setUniform("time", ShaderUtils.getTime());
            
            // Draw a fullscreen quad
            myCustomShader.drawFullscreenQuad();
        }
    };

    // Add the effect to the chain
    LensRendering.POST_CHAIN.addEffect(myEffect);
}
```

This registration should be done during your module's initialization.

### Further Examples

For more detailed examples of how to create post-processing effects, please see the `ShaderEffectTest` class, which demonstrates how to create several different screen effects.

## Other Rendering Examples

In addition to the post-processing chain, `LensRendering` also contains several other methods that serve as examples of how to use various features of the Lens rendering engine. While some of these may not be actively used, they can be a valuable resource for learning how to implement your own rendering logic.
