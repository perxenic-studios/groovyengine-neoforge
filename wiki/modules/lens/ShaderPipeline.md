# Shader Pipeline System

The Shader Pipeline is a comprehensive system for managing and executing shaders in Minecraft, providing support for traditional rendering pipelines, compute shaders, and post-processing effects.

## Overview

The system is built around several core components:

- **Shader Management**: Base shader compilation and management
- **Compute Shaders**: GPU-accelerated computation
- **Post-Processing**: Chainable visual effects
- **Utilities**: Common patterns and helper functions

## Core Components

### Shader Base Classes

#### Shader (Abstract Base Class)
The foundation for all shader types in the system.

```java
// Basic usage
VertexShader vertex = new VertexShader(vertexSource);
FragmentShader fragment = new FragmentShader(fragmentSource);
```

**Key Features:**
- Automatic compilation and error checking
- Resource loading from classpath
- Proper cleanup and resource management
- Support for all OpenGL shader types

#### Shader Types
- `VertexShader` - Vertex processing
- `FragmentShader` - Pixel/fragment processing
- `GeometryShader` - Geometry processing (OpenGL 3.2+)
- `ComputeShader` - General-purpose computation (OpenGL 4.3+)

### ShaderProgram

Manages linked shader programs and uniform handling.

```java
ShaderProgram program = new ShaderProgram()
    .addShader(vertexShader)
    .addShader(fragmentShader)
    .link();

program.bind();
program.setUniform("color", 1.0f, 0.0f, 0.0f);
program.drawFullscreenQuad();
program.unbind();
```

**Features:**
- Automatic shader attachment and linking
- Cached uniform locations
- Fullscreen quad rendering
- Matrix and vector uniform support
- Proper resource cleanup

### ComputeShader

GPU-accelerated computation using OpenGL compute shaders.

```java
// Create compute shader with initial data
float[] data = new float[1000];
ComputeShader compute = new ComputeShader(data, configLocation);

// Execute computation
compute.dispatch(128, 1, 1);

// Read back results
float[] results = compute.readBackFloats();
```

**Key Capabilities:**
- Shader Storage Buffer Objects (SSBO)
- Multiple data types (float, int, custom)
- Automatic memory management
- Result readback utilities
- Config-based shader loading

### Post-Processing System

#### PostProcessChain
Manages sequences of post-processing effects with ping-pong buffering.

```java
PostProcessChain chain = new PostProcessChain()
    .addEffect(createBloomEffect())
    .addEffect(createColorGradingEffect())
    .addEffect(createVignetteEffect());

// Execute the entire chain
chain.execute();
```

**Features:**
- Automatic ping-pong buffering
- Dynamic buffer resizing
- Effect sequencing
- Minimal state disruption

#### Creating Effects
```java
PostProcessChain.PostProcessEffect bloomEffect = new PostProcessChain.PostProcessEffect() {
    @Override
    public void apply() {
        bloomShader.bind();
        bloomShader.setUniform("intensity", 2.0f);
        bloomShader.drawFullscreenQuad();
    }
};
```

## Utility Systems

### ShaderUtils

Provides common functionality and uniform management.

```java
// Common uniform setup
ShaderUtils.CommonUniforms.setAllUniforms(shader);

// Quick post-processing
ShaderUtils.PostProcessHelper helper = new ShaderUtils.PostProcessHelper(shader);
helper.execute();
```

**Uniform Categories:**
- **Screen**: Resolution, aspect ratio, textures
- **Time**: Game time, delta time
- **Camera**: Position, direction, matrices
- **Projection**: FOV, near/far planes

### Preset Effects

Common shader effects with pre-configured uniforms:

```java
// Chromatic aberration
ShaderUtils.UniformPresets.setChromaticAberrationUniforms(shader, 0.02f, 0.4f);

// Blur effects  
ShaderUtils.UniformPresets.setBlurUniforms(shader, 5.0f);

// Color grading
ShaderUtils.UniformPresets.setColorGradingUniforms(shader, 1.2f, 1.1f, 1.0f);
```

## Usage Examples

### Basic Post-Processing

```java
public class BasicPostProcessor {
    private ShaderProgram effectShader;
    private PostProcessChain chain;
    
    public void init() {
        // Create shader
        effectShader = new ShaderProgram()
            .addShader(VertexShader.createFullscreen())
            .addShader(new FragmentShader(effectSource))
            .link();
            
        // Create effect
        PostProcessChain.PostProcessEffect effect = new PostProcessChain.PostProcessEffect() {
            @Override
            public void apply() {
                effectShader.bind();
                ShaderUtils.CommonUniforms.setAllUniforms(effectShader);
                effectShader.setUniform("customParam", 1.0f);
                effectShader.drawFullscreenQuad();
            }
        };
        
        // Setup chain
        chain = new PostProcessChain().addEffect(effect);
    }
    
    public void render() {
        chain.execute();
    }
}
```

### Compute Shader with Custom Data

```java
public class ParticleSystem {
    private ComputeShader particleCompute;
    private float[] particleData;
    
    public void init() {
        // Initialize particle data
        particleData = new float[PARTICLE_COUNT * 4]; // x, y, vx, vy
        
        // Create compute shader
        particleCompute = new ComputeShader(
            particleData, 
            Float.BYTES, 
            new ResourceLocation("mymod:particles")
        );
    }
    
    public void update() {
        // Update particles on GPU
        particleCompute.dispatch(PARTICLE_COUNT / 128, 1, 1);
        
        // Optional: read back for CPU usage
        float[] updatedData = particleCompute.readBackFloats();
    }
}
```

### Advanced Effect Chain

```java
public class AdvancedEffects {
    private PostProcessChain chain;
    
    public void init() {
        chain = new PostProcessChain()
            .addEffect(createDepthOfField())
            .addEffect(createBloom())
            .addEffect(createToneMapping())
            .addEffect(createFilmGrain());
    }
    
    private PostProcessChain.PostProcessEffect createDepthOfField() {
        return new PostProcessChain.PostProcessEffect() {
            @Override
            public void apply() {
                dofShader.bind();
                // Bind depth texture and set focus parameters
                dofShader.setUniform("focusDistance", 10.0f);
                dofShader.setUniform("aperture", 0.1f);
                dofShader.drawFullscreenQuad();
            }
        };
    }
    
    public void render() {
        chain.execute();
    }
}
```

## File Structure

### Shader Configuration
Compute shaders use JSON configuration:

```json
// assets/mymod/shaders/compute/particles.json
{
    "compute": "mymod:particle_update"
}
```

### Shader Files
- **Vertex**: `.vsh` or embedded strings
- **Fragment**: `.fsh` or embedded strings
- **Compute**: `.csh` files
- **Geometry**: `.gsh` files

### Recommended Organization
```
assets/
└── mymod/
    └── shaders/
        ├── compute/
        │   ├── particle_update.csh
        │   └── particle_update.json
        ├── postprocess/
        │   ├── bloom.fsh
        │   └── color_grading.fsh
        └── utility/
            ├── fullscreen.vsh
            └── common.fsh
```

## Best Practices

### Performance
1. **Minimize State Changes**: Batch uniform updates
2. **Reuse Shaders**: Cache compiled shader programs
3. **Buffer Management**: Use SSBOs for large data transfers
4. **Memory Barriers**: Use appropriate barriers in compute shaders

### Code Organization
1. **Resource Management**: Always implement `dispose()` methods
2. **Error Handling**: Check shader compilation status
3. **Thread Safety**: Execute on render thread only
4. **Configuration**: Use JSON configs for complex shaders

### Compatibility
1. **Feature Detection**: Check support before using advanced features
2. **Fallbacks**: Provide CPU fallbacks for compute shaders
3. **Version Checking**: Target appropriate GLSL versions

## Advanced Topics

### Custom Data Types
```java
// Custom struct in shader
public class Particle {
    public float x, y, z;
    public float vx, vy, vz;
    public float life;
}

// SSBO with custom data
ByteBuffer particleBuffer = createParticleBuffer(particles);
ComputeShader compute = new ComputeShader(particleBuffer, Particle.SIZE, shaderConfig);
```

### Multi-Pass Effects
```java
// Multi-pass blur
public PostProcessChain.PostProcessEffect createBlurEffect() {
    return new PostProcessChain.PostProcessEffect() {
        @Override
        public void apply() {
            blurShader.bind();
            
            // Horizontal pass
            blurShader.setUniform("direction", 1.0f, 0.0f);
            blurShader.drawFullscreenQuad();
            
            // Vertical pass  
            blurShader.setUniform("direction", 0.0f, 1.0f);
            blurShader.drawFullscreenQuad();
        }
    };
}
```

### Dynamic Uniforms
```java
// Time-based animations
shader.setUniform("time", ShaderUtils.getTime());
shader.setUniform("sinTime", (float)Math.sin(ShaderUtils.getTime()));

// Camera-relative effects
var camera = Minecraft.getInstance().gameRenderer.getMainCamera();
shader.setUniform("cameraPos", 
    (float)camera.getPosition().x,
    (float)camera.getPosition().y, 
    (float)camera.getPosition().z
);
```

## Troubleshooting

### Common Issues

1. **Shader Compilation Errors**
    - Check GLSL version compatibility
    - Verify shader source loading
    - Examine compilation logs

2. **Compute Shader Support**
   ```java
   if (!ComputeShader.isSupported()) {
       // Fallback to CPU implementation
   }
   ```

3. **Uniform Location Errors**
    - Use cached uniform locations
    - Check uniform name spelling
    - Verify shader program is linked

4. **Performance Problems**
    - Minimize texture binds
    - Use appropriate work group sizes
    - Avoid frequent buffer updates

### Debugging Tips

1. Enable OpenGL debug output
2. Check shader info logs
3. Validate framebuffer status
4. Use render doc for GPU debugging

This pipeline provides a robust foundation for both traditional rendering and modern GPU compute workflows, with particular strength in post-processing and visual effects.
