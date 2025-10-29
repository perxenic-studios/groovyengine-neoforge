# PoseScope

`PoseScope` is a utility that simplifies working with Minecraft's `PoseStack`, which is used to manage transformations (translation, rotation, scaling) during rendering. It provides a convenient way to apply transformations without needing to manually call `pushPose()` and `popPose()`.

This utility also integrates with [RenderUtils](RenderUtils.md) to allow for easy in-world rendering.

## Usage

The `PoseScope` is best used with a `try-with-resources` statement or a lambda, which ensures that the `PoseStack` is always popped correctly, even if an error occurs.

### With a Lambda

```java
public static void render(PoseStack stack) {
    new PoseScope(stack)
        .world()
        .run(poseStack -> {
            poseStack.translate(1, 2, 3);
            // Your rendering code here
        });
}
```

### With Try-With-Resources

```java
public static void render(PoseStack stack) {
    try (PoseScope scope = new PoseScope(stack).world()) {
        scope.stack().translate(1, 2, 3);
        // Your rendering code here
    }
}
```

Both of these examples will automatically push the `PoseStack` at the beginning of the block and pop it at the end.

For a complete list of methods, please see the [PoseScope Javadoc](../html/classio_1_1github_1_1luckymcdev_1_1groovyengine_1_1lens_1_1client_1_1rendering_1_1util_1_1_pose_scope.html).
