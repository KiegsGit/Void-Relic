package com.voidrelic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

/** The location saved on a Void Relic by the Rift Anchor. Stored on the item stack. */
public record RiftAnchorData(ResourceKey<Level> dimension, double x, double y, double z, float yRot, float xRot) {

    public static final Codec<RiftAnchorData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Level.RESOURCE_KEY_CODEC.fieldOf("dimension").forGetter(RiftAnchorData::dimension),
            Codec.DOUBLE.fieldOf("x").forGetter(RiftAnchorData::x),
            Codec.DOUBLE.fieldOf("y").forGetter(RiftAnchorData::y),
            Codec.DOUBLE.fieldOf("z").forGetter(RiftAnchorData::z),
            Codec.FLOAT.fieldOf("yaw").forGetter(RiftAnchorData::yRot),
            Codec.FLOAT.fieldOf("pitch").forGetter(RiftAnchorData::xRot)
    ).apply(instance, RiftAnchorData::new));
}
