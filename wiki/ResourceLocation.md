# ResourceLocation

A `ResourceLocation` is a unique identifier used by Minecraft to reference assets, such as textures, sounds, and models. It consists of two parts: a **namespace** and a **path**.

For example, the `ResourceLocation` for the furnace GUI texture is:
`"minecraft:textures/gui/container/furnace.png"`

- **Namespace:** `minecraft`
- **Path:** `textures/gui/container/furnace.png`

In GroovyEngine, you will use `ResourceLocation`s to reference assets within your project's `resources` folder. The namespace for your project is defined by the `mod.id` in your `build.gradle` file. By default, this is `groovyengine`.

For example, if you have an image located at:
`.../resources/assets/groovyengine/textures/custom_gui.png`

The `ResourceLocation` for this texture would be:
`"groovyengine:textures/custom_gui.png"`
