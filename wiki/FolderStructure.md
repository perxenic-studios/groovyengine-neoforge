# Folder Structure

The folder structure of groovyengine is as follows:

```
.minecraft/
├── groovyengine/
│   ├── src/
│   │   └── main/
│   │       ├── groovy/
│   │       │   ├── client
│   │       │   ├── common
│   │       │   └── server
│   │       └── resources/
│   │           ├── assets
│   │           └── data
│   └── modules
├── build.gradle
└── gradle.properties
```

It works like most mods do. Where build.gradle and gradle.properites is advanced
and shouldnt really be accesed by users.

Most of the things domne by the user should be in `source/main` where the 
`groovy` folder is the root of your scripts. the `client`, `common` and `server` are
loaded on the respective side, see [Sides](Sides)

The `resources` folder is for minecraft `assets` and `data`. `assets` work like a resource pack, see 
[Minecraft Documentation on Resource Packs](https://minecraft.fandom.com/wiki/Resource_pack)
and `data` is for data packs, see 
[Minecraft Documentation on Data Packs](https://minecraft.fandom.com/wiki/Data_pack).

Most of that is applicable here.