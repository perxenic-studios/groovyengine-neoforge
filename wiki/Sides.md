# Sides

Minecraft's code operates on two conceptual layers: the physical and the logical. Understanding this distinction is crucial for writing side-safe scripts.

## Logical vs. Physical Sides

### Physical Side
- The **physical client** is the Minecraft client application, responsible for rendering, input, and the graphical user interface (GUI).
- The **physical server** is the dedicated server process, which lacks client-only rendering classes.
- In single-player mode, the physical client runs both a logical server and a logical client on the same machine.

### Logical Side
- The **logical server** manages the game state, including ticks, entity updates, and world changes.
- The **logical client** handles rendering and other client-only behaviors, such as the render thread and GUI.
- Code that interacts with rendering, textures, or classes within the `net.minecraft.client` package must only execute on the logical and physical client-side.

## Why This Matters

- Referencing client-only classes from server-side code will lead to crashes (`ClassNotFoundException` or `NoClassDefFoundError`) on dedicated servers.
- Static fields and shared singletons can behave unexpectedly if you assume a single JVM-side context. Always test on a dedicated server to ensure proper separation.

## Practical Guidance

- Organize your scripts into `client/`, `common/`, and `server/` folders:
    - `client/`: For rendering, HUDs, shader code, and client-only hooks.
    - `common/`: For logic that can be used on both the client and server.
    - `server/`: For world ticks, spawn logic, and persistent state.
- When dynamic checks are necessary, use side-checks.
    - In environments like NeoForge or Forge, use enums such as `Dist` (for the physical side) and `LogicalSide` to guard client-only code paths.
- To communicate between sides, use networking (packets) or the engine's synchronization APIs.

## Tip

- Always test your code on a dedicated server in addition to single-player to identify side-separation issues early.

## Credits

- Conceptual reference from [docs.neoforged.dev](https.docs.neoforged.dev).