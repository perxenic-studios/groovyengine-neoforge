package io.github.luckymcdev.groovyengine.lens.client.rendering.stage;

import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

public enum RenderStage {
    AFTER_SKY(RenderLevelStageEvent.Stage.AFTER_SKY),
    AFTER_CUTOUT_MIPPED_BLOCKS(RenderLevelStageEvent.Stage.AFTER_CUTOUT_MIPPED_BLOCKS_BLOCKS),
    AFTER_CUTOUT_BLOCKS(RenderLevelStageEvent.Stage.AFTER_CUTOUT_BLOCKS),
    AFTER_ENTITIES(RenderLevelStageEvent.Stage.AFTER_ENTITIES),
    AFTER_PARTICLES(RenderLevelStageEvent.Stage.AFTER_PARTICLES),
    AFTER_WEATHER(RenderLevelStageEvent.Stage.AFTER_WEATHER),
    AFTER_SOLID_BLOCKS(RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS),
    AFTER_TRIPWIRE_BLOCKS(RenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS),
    AFTER_TRANSLUCENT_BLOCKS(RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS);

    private final RenderLevelStageEvent.Stage forgeStage;

    RenderStage(RenderLevelStageEvent.Stage forgeStage) {
        this.forgeStage = forgeStage;
    }

    /**
     * Get the underlying Forge stage
     */
    public RenderLevelStageEvent.Stage get() {
        return forgeStage;
    }


    /**
     * Get the display name of this stage
     */
    public String getDisplayName() {
        return name().toLowerCase().replace('_', ' ');
    }


    @Override
    public String toString() {
        return getDisplayName();
    }
}