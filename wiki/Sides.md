# Sides

Minecraft code runs in two conceptual layers — physical and logical — and understanding this helps you write side-safe scripts.

Logical vs. Physical Side

Physical side
- The "physical client" is the running Minecraft client application (with rendering, input, and GUI).
- The "physical server" is a dedicated server process (no client-only rendering classes).
- When you run singleplayer, your physical client runs both a logical server and a logical client on the same machine.

Logical side
- The logical server handles game state, ticks, entity updates, world changes, etc.
- The logical client handles rendering and client-only behavior (render thread, GUI).
- Code that touches rendering, textures, or net.minecraft.client.* classes must only run on the client logical/physical side.

Why this matters
- Referencing client-only classes from server-side code will cause crashes (ClassNotFoundException / NoClassDefFoundError) on dedicated servers.
- Static fields and shared singletons can behave unexpectedly if you assume a single JVM-side context; test on dedicated servers to ensure separation works.

Practical guidance
- Separate scripts into `client/`, `common/`, and `server/` folders as appropriate.
    - client/ — rendering, HUDs, shader code, client-only hooks
    - common/ — logic usable on both sides
    - server/ — world ticks, spawn logic, persistent state
- Use side-checks when dynamic checks are needed:
    - In NeoForge/Forge-like environments there are enums such as `Dist` (physical side) and `LogicalSide` (logical side). Use the runtime-provided checks to guard client-only code paths.
- If you need to communicate between sides, use networking (packets) or the engine's provided synchronization APIs.

Tip
- Always test with a dedicated server in addition to singleplayer to catch side separation issues early.

Credits
- Courtesy to docs.neoforged.dev for conceptual reference.