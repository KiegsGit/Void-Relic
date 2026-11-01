package com.voidrelic;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/** Pulls dropped items and XP orbs toward any player holding a Void Relic. Server-side only. */
public final class VoidMagnet {

    public static final double RADIUS = 8.0;
    private static final int MAX_PARTICLE_ENTITIES = 20;

    private VoidMagnet() {
    }

    public static void tick(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (!player.isAlive() || player.isSpectator()) {
                continue;
            }
            if (!player.getMainHandItem().is(ModItems.VOID_RELIC) && !player.getOffhandItem().is(ModItems.VOID_RELIC)) {
                continue;
            }
            try {
                pull(server, (ServerLevel) player.level(), player);
            } catch (RuntimeException e) {
                VoidRelicMod.LOGGER.error("Void Magnet failed", e);
            }
        }
    }

    private static void pull(MinecraftServer server, ServerLevel level, ServerPlayer player) {
        Vec3 target = player.position().add(0.0, player.getBbHeight() * 0.5, 0.0);
        AABB area = player.getBoundingBox().inflate(RADIUS);
        double radiusSqr = RADIUS * RADIUS;
        boolean particleTick = server.getTickCount() % 2 == 0;
        int shown = 0;

        for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class, area,
                e -> e.isAlive() && !e.getItem().isEmpty() && !e.hasPickUpDelay() && e.distanceToSqr(player) <= radiusSqr)) {
            attract(item, target);
            if (particleTick && shown++ < MAX_PARTICLE_ENTITIES) {
                trail(level, item);
            }
        }

        for (ExperienceOrb orb : level.getEntitiesOfClass(ExperienceOrb.class, area,
                e -> e.isAlive() && e.distanceToSqr(player) <= radiusSqr)) {
            attract(orb, target);
            if (particleTick && shown++ < MAX_PARTICLE_ENTITIES) {
                trail(level, orb);
            }
        }

        // Subtle glow around the held relic.
        if (server.getTickCount() % 12 == 0) {
            Vec3 look = player.getLookAngle();
            level.sendParticles(ParticleTypes.WITCH,
                    player.getX() + look.x * 0.5, player.getEyeY() - 0.35, player.getZ() + look.z * 0.5,
                    1, 0.12, 0.12, 0.12, 0.0);
        }
    }

    /** Eases the entity's velocity toward the player instead of teleporting it. */
    private static void attract(Entity entity, Vec3 target) {
        Vec3 toTarget = target.subtract(entity.position());
        double distance = toTarget.length();
        if (distance < 0.6) {
            return; // close enough for vanilla pickup to take over
        }
        double speed = 0.12 + 0.28 * (1.0 - Math.min(distance, RADIUS) / RADIUS);
        Vec3 desired = toTarget.scale(speed / distance);
        Vec3 change = desired.subtract(entity.getDeltaMovement()).scale(0.25);
        entity.push(change.x, change.y, change.z);
    }

    private static void trail(ServerLevel level, Entity entity) {
        level.sendParticles(ParticleTypes.PORTAL, entity.getX(), entity.getY() + 0.2, entity.getZ(),
                1, 0.1, 0.1, 0.1, 0.0);
    }
}
