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

package io.github.luckymcdev.groovyengine.lens.rendering.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;

public class ParticleBuilder {
    private final ClientLevel level;
    private ParticleOptions particleType = ParticleTypes.FLAME;
    private double x, y, z;
    private double xSpeed, ySpeed, zSpeed;
    private boolean forceAlwaysRender = false;

    private ParticleBuilder(ClientLevel level, double x, double y, double z) {
        this.level = level;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static ParticleBuilder create(ClientLevel level, double x, double y, double z) {
        return new ParticleBuilder(level, x, y, z);
    }

    public static ParticleBuilder create(ClientLevel level, Vec3 pos) {
        return new ParticleBuilder(level, pos.x, pos.y, pos.z);
    }

    public ParticleBuilder setType(ParticleOptions particleType) {
        this.particleType = particleType;
        return this;
    }

    public ParticleBuilder setType(SimpleParticleType particleType) {
        this.particleType = particleType;
        return this;
    }

    // Convenience methods for common particle types
    public ParticleBuilder flame() {
        return setType(ParticleTypes.FLAME);
    }

    public ParticleBuilder smoke() {
        return setType(ParticleTypes.SMOKE);
    }

    public ParticleBuilder spark() {
        return setType(ParticleTypes.FLASH);
    }

    public ParticleBuilder heart() {
        return setType(ParticleTypes.HEART);
    }

    public ParticleBuilder note() {
        return setType(ParticleTypes.NOTE);
    }

    public ParticleBuilder portal() {
        return setType(ParticleTypes.PORTAL);
    }

    public ParticleBuilder setPosition(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public ParticleBuilder setPosition(Vec3 pos) {
        this.x = pos.x;
        this.y = pos.y;
        this.z = pos.z;
        return this;
    }

    public ParticleBuilder setMotion(double xSpeed, double ySpeed, double zSpeed) {
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        this.zSpeed = zSpeed;
        return this;
    }

    public ParticleBuilder setMotion(Vec3 motion) {
        this.xSpeed = motion.x;
        this.ySpeed = motion.y;
        this.zSpeed = motion.z;
        return this;
    }

    public ParticleBuilder randomMotion(double maxSpeed) {
        this.xSpeed = (Math.random() - 0.5) * 2 * maxSpeed;
        this.ySpeed = (Math.random() - 0.5) * 2 * maxSpeed;
        this.zSpeed = (Math.random() - 0.5) * 2 * maxSpeed;
        return this;
    }

    public ParticleBuilder upwardMotion(double speed) {
        this.ySpeed = speed;
        return this;
    }

    public ParticleBuilder downwardMotion(double speed) {
        this.ySpeed = -speed;
        return this;
    }

    public ParticleBuilder setForceAlwaysRender(boolean forceAlwaysRender) {
        this.forceAlwaysRender = forceAlwaysRender;
        return this;
    }

    public ParticleBuilder alwaysRender() {
        return setForceAlwaysRender(true);
    }

    public void spawn() {
        if (forceAlwaysRender) {
            level.addParticle(particleType, true, x, y, z, xSpeed, ySpeed, zSpeed);
        } else {
            level.addParticle(particleType, x, y, z, xSpeed, ySpeed, zSpeed);
        }
    }

    // Method to spawn multiple particles at once
    public void spawnMultiple(int count) {
        for (int i = 0; i < count; i++) {
            spawn();
        }
    }

    // Method to spawn particles in a burst pattern
    public void spawnBurst(int count, double spread) {
        for (int i = 0; i < count; i++) {
            double spreadX = (Math.random() - 0.5) * 2 * spread;
            double spreadY = (Math.random() - 0.5) * 2 * spread;
            double spreadZ = (Math.random() - 0.5) * 2 * spread;

            if (forceAlwaysRender) {
                level.addParticle(particleType, true,
                        x + spreadX, y + spreadY, z + spreadZ,
                        xSpeed, ySpeed, zSpeed);
            } else {
                level.addParticle(particleType,
                        x + spreadX, y + spreadY, z + spreadZ,
                        xSpeed, ySpeed, zSpeed);
            }
        }
    }

    // Chainable spawn method for one-liners
    public ParticleBuilder andSpawn() {
        spawn();
        return this;
    }
}