# OBJ and AMO Rendering

GroovyEngine provides a robust system for rendering 3D models in the `.obj` format, as well as a custom `.amo` format for animated models. For more information on the `.amo` format, see the [animated_obj repository](https://github.com/lmarz/animated_obj).

## 1. Registering a Model

Before you can render a model, you must first register it with the `ObjModelManager`. This is done by creating a new `ObjModel` or `AmoModel` instance and passing it to the manager.

```java
// For a static .obj model
ObjModel myObjModel = new ObjModel(new ResourceLocation("my_mod:models/my_model.obj"));
ObjModelManager.register(myObjModel);

// For an animated .amo model
AmoModel myAmoModel = new AmoModel(new ResourceLocation("my_mod:models/my_animated_model.amo"));
ObjModelManager.register(myAmoModel);
```

## 2. Rendering a Model

Once a model has been registered, you can render it by calling its `render()` method.

### Rendering a Static `.obj` Model

To render a static model, you need to provide a `PoseStack`, a `Material`, and the packed light value.

```java
myObjModel.render(poseStack, myMaterial, packedLight);
```

### Rendering an Animated `.amo` Model

Rendering an animated model is similar to rendering a static one, but with a few extra steps.

First, you must call the `updateAnimation()` method every frame to advance the animation.

```java
myAmoModel.updateAnimation(partialTicks);
```

You can then play a specific animation by name:

```java
myAmoModel.playAnimation("my_animation_name");
```

Finally, you can render the model using the `renderAnimated()` method:

```java
myAmoModel.renderAnimated(poseStack, myMaterial, packedLight);
```

For a complete example of how to use the OBJ rendering system, please see the `LensRendering` class.
