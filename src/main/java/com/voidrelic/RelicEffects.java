package com.voidrelic;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

/** Particles and sounds. Everything here is sent from the server, so all nearby players see it. */
public final class RelicEffects {

    private RelicEffects() {
    }

    /** A rising purple helix with a portal burst at its base: the Rift Anchor being set. */
    public static void anchorSet(ServerLevel level, Vec3 pos) {
        helix(level, pos, ParticleTypes.PORTAL, 0.9, 2.2, 36, 0.0);
        helix(level, pos, ParticleTypes.REVERSE_PORTAL, 0.6, 2.2, 24, Math.PI);
        level.sendParticles(ParticleTypes.PORTAL, pos.x, pos.y + 1.0, pos.z, 50, 0.4, 0.8, 0.4, 0.6);
        level.sendParticles(ParticleTypes.END_ROD, pos.x, pos.y + 1.0, pos.z, 8, 0.3, 0.6, 0.3, 0.02);
        level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 0.9F, 1.4F);
        level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0F, 0.7F);
    }

    /** Collapsing ring at the departure point. */
    public static void departure(ServerLevel level, Vec3 pos) {
        helix(level, pos, ParticleTypes.PORTAL, 0.8, 2.0, 30, 0.0);
        level.sendParticles(ParticleTypes.REVERSE_PORTAL, pos.x, pos.y + 1.0, pos.z, 60, 0.3, 0.7, 0.3, 0.15);
        level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.8F, 0.8F);
    }

    /** Burst at the arrival point. */
    public static void arrival(ServerLevel level, Vec3 pos) {
        level.sendParticles(ParticleTypes.PORTAL, pos.x, pos.y + 1.0, pos.z, 80, 0.5, 0.9, 0.5, 0.9);
        level.sendParticles(ParticleTypes.END_ROD, pos.x, pos.y + 1.0, pos.z, 10, 0.4, 0.7, 0.4, 0.03);
        helix(level, pos, ParticleTypes.REVERSE_PORTAL, 0.8, 2.0, 24, 0.0);
        level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.9F, 1.3F);
        level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.PLAYERS, 1.0F, 0.8F);
    }

    /** Short dull sound used when the relic is still recharging or the destination is unsafe. */
    public static void denied(ServerLevel level, Vec3 pos) {
        level.sendParticles(ParticleTypes.SMOKE, pos.x, pos.y + 1.0, pos.z, 6, 0.25, 0.4, 0.25, 0.01);
        level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 0.6F, 1.2F);
    }

    private static void helix(ServerLevel level, Vec3 base, ParticleOptions particle,
                              double radius, double height, int points, double phase) {
        for (int i = 0; i < points; i++) {
            double t = (double) i / points;
            double angle = phase + t * Math.PI * 6.0;
            double r = radius * (1.0 - 0.35 * t);
            level.sendParticles(particle,
                    base.x + Math.cos(angle) * r,
                    base.y + t * height,
                    base.z + Math.sin(angle) * r,
                    1, 0.0, 0.0, 0.0, 0.0);
        }
    }
}
