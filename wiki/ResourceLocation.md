# ResourceLocation

A `ResourceLocation` is a unique identifier used by Minecraft to reference assets, such as textures, sounds, and models. It consists of two parts: a **namespace** and a **path**.

For example, the `ResourceLocation` for the furnace GUI texture is:
`"minecraft:textures/gui/container/furnace.png"`

- **Namespace:** `minecraft`
- **Path:** `textures/gui/container/furnace.png`

In GroovyEngine, you will use `ResourceLocation`s to reference assets within your project's `resources` folder. 
The namespace for your project is defined by the `module_name` in your `gradle.properties` file. By default, this is `mymodule`.

For example, if you have an image located at:
`.../resources/assets/yourmodule/textures/custom_gui.png`

The `ResourceLocation` for this texture would be:
`"yourmodule:textures/custom_gui.png"`
