package com.voidrelic;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public final class ModComponents {

    /** Saved Rift Anchor location. Persisted with the stack, never sent to clients. */
    public static final DataComponentType<RiftAnchorData> RIFT_ANCHOR = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            Identifier.fromNamespaceAndPath(VoidRelicMod.MOD_ID, "rift_anchor"),
            DataComponentType.<RiftAnchorData>builder().persistent(RiftAnchorData.CODEC).build());

    private ModComponents() {
    }

    public static void init() {
        // Loading this class registers the component.
    }
}
