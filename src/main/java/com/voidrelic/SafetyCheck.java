package com.voidrelic;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/** Decides whether a saved Rift Anchor can be teleported to without killing the player. */
public final class SafetyCheck {

    /** How far below the feet we accept ground (so a few blocks of fall are fine, a void is not). */
    private static final int MAX_DROP = 4;

    private SafetyCheck() {
    }

    public static boolean isSafe(ServerLevel level, Player player, Vec3 pos) {
        BlockPos feet = BlockPos.containing(pos);

        if (!level.isInWorldBounds(feet) || !level.getWorldBorder().isWithinBounds(feet)) {
            return false;
        }
        if (!level.hasChunkAt(feet)) {
            return false;
        }

        // Room for the player's body.
        AABB box = player.getDimensions(Pose.STANDING).makeBoundingBox(pos);
        if (!level.noCollision(box)) {
            return false;
        }

        // Nothing harmful inside the player's body space.
        BlockPos min = BlockPos.containing(box.minX, box.minY, box.minZ);
        BlockPos max = BlockPos.containing(box.maxX, box.maxY, box.maxZ);
        for (BlockPos p : BlockPos.betweenClosed(min, max)) {
            if (isHazard(level.getBlockState(p))) {
                return false;
            }
        }

        // Solid, non-harmful ground (or water) within a short drop; no void, no lava below.
        for (int i = 0; i <= MAX_DROP; i++) {
            BlockPos below = feet.below(i);
            if (!level.isInWorldBounds(below)) {
                return false;
            }
            BlockState state = level.getBlockState(below);
            if (state.getFluidState().is(FluidTags.LAVA) || isHazard(state)) {
                return false;
            }
            if (!state.getCollisionShape(level, below).isEmpty() || !state.getFluidState().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private static boolean isHazard(BlockState state) {
        return state.getFluidState().is(FluidTags.LAVA)
                || state.is(BlockTags.FIRE)
                || state.is(BlockTags.CAMPFIRES)
                || state.is(Blocks.MAGMA_BLOCK)
                || state.is(Blocks.CACTUS)
                || state.is(Blocks.SWEET_BERRY_BUSH)
                || state.is(Blocks.WITHER_ROSE)
                || state.is(Blocks.POWDER_SNOW);
    }
}
