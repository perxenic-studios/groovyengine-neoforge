/*
 * Copyright 2025 LuckyMcDev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.luckymcdev.groovyengine.lens.client.rendering.pipeline.shader;

public class FragmentShader extends Shader {
    public FragmentShader(String source) {
        super(ShaderType.FRAGMENT, source);
    }

    public FragmentShader() {
        super(ShaderType.FRAGMENT);
    }

    /**
     * Create a simple passthrough fragment shader
     */
    public static FragmentShader createPassthrough() {
        String source = """
                #version 330 core
                in vec2 texCoord;
                out vec4 fragColor;
                uniform sampler2D screenTexture;
                void main() {
                    fragColor = texture(screenTexture, texCoord);
                }
                """;
        return new FragmentShader(source);
    }
}