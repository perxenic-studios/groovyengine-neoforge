# Folder Structure

The GroovyEngine directory is located inside your `.minecraft` folder and is organized as follows:

```
.minecraft/
└── groovyengine/
    ├── workspace/
    │   ├── src/
    │   │   └── main/
    │   │       ├── groovy/
    │   │       │   ├── client/
    │   │       │   ├── common/
    │   │       │   └── server/
    │   │       └── resources/
    │   │           ├── assets/
    │   │           └── data/
    │   ├── build.gradle
    │   └── gradle.properties
    └── modules/
```

### `workspace/`

This is the primary directory for all your project files.

- **`src/main/groovy/`**: This is the root directory for your Groovy scripts. For more information on how to write scripts, see the [Scripts](Scripts.md) page.
    - **`client/`**: Scripts in this folder will only load on the client side.
    - **`common/`**: Scripts in this folder will load on both the client and server sides.
    - **`server/`**: Scripts in this folder will only load on the server side.
    
    For a more detailed explanation of how sides work, see the [Sides](Sides.md) page.

- **`src/main/resources/`**: This folder is for your project's assets and data.
    - **`assets/`**: This folder functions like a standard Minecraft resource pack. For more information, see the [Minecraft Wiki article on Resource Packs](https://minecraft.fandom.com/wiki/Resource_pack).
    - **`data/`**: This folder functions like a standard Minecraft data pack. For more information, see the [Minecraft Wiki article on Data Packs](https://minecraft.fandom.com/wiki/Data_pack).

- **`build.gradle` and `gradle.properties`**: These files are for advanced users and should not be modified unless you know what you are doing.

### `modules/`

This directory contains the various modules that make up GroovyEngine. For more information, see the [Modules](modules/Modules.md) page.
