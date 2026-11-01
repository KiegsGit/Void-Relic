package com.voidrelic;

import java.util.Set;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class VoidRelicItem extends Item {

    /** 90 seconds. */
    public static final int COOLDOWN_TICKS = 90 * 20;

    public VoidRelicItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        // The server decides everything; the client only swings the arm.
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (!(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.PASS;
        }

        ItemStack stack = player.getItemInHand(hand);
        try {
            return player.isShiftKeyDown()
                    ? recall(serverLevel, serverPlayer, stack)
                    : setAnchor(serverLevel, serverPlayer, stack);
        } catch (RuntimeException e) {
            VoidRelicMod.LOGGER.error("Void Relic failed while being used", e);
            message(player, "message.void_relic.error", ChatFormatting.RED);
            return InteractionResult.FAIL;
        }
    }

    private InteractionResult setAnchor(ServerLevel level, ServerPlayer player, ItemStack stack) {
        stack.set(ModComponents.RIFT_ANCHOR, new RiftAnchorData(
                level.dimension(), player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot()));

        RelicEffects.anchorSet(level, player.position());
        player.displayClientMessage(Component.translatable("message.void_relic.anchor_set",
                Math.round(player.getX()), Math.round(player.getY()), Math.round(player.getZ()))
                .withStyle(ChatFormatting.LIGHT_PURPLE), true);
        return InteractionResult.SUCCESS;
    }

    private InteractionResult recall(ServerLevel level, ServerPlayer player, ItemStack stack) {
        RiftAnchorData anchor = stack.get(ModComponents.RIFT_ANCHOR);
        if (anchor == null) {
            message(player, "message.void_relic.no_anchor", ChatFormatting.GRAY);
            RelicEffects.denied(level, player.position());
            return InteractionResult.FAIL;
        }

        if (!anchor.dimension().equals(level.dimension())) {
            message(player, "message.void_relic.wrong_dimension", ChatFormatting.RED);
            RelicEffects.denied(level, player.position());
            return InteractionResult.FAIL;
        }

        if (player.getCooldowns().isOnCooldown(stack)) {
            int seconds = (int) Math.ceil(player.getCooldowns().getCooldownPercent(stack, 0.0F) * (COOLDOWN_TICKS / 20.0));
            player.displayClientMessage(Component.translatable("message.void_relic.cooldown", Math.max(1, seconds))
                    .withStyle(ChatFormatting.RED), true);
            RelicEffects.denied(level, player.position());
            return InteractionResult.FAIL;
        }

        Vec3 destination = new Vec3(anchor.x(), anchor.y(), anchor.z());
        if (!SafetyCheck.isSafe(level, player, destination)) {
            message(player, "message.void_relic.unsafe", ChatFormatting.RED);
            RelicEffects.denied(level, player.position());
            return InteractionResult.FAIL;
        }

        RelicEffects.departure(level, player.position());
        player.teleportTo(level, destination.x, destination.y, destination.z, Set.of(), anchor.yRot(), anchor.xRot(), true);
        player.resetFallDistance();
        RelicEffects.arrival(level, destination);

        player.getCooldowns().addCooldown(stack, COOLDOWN_TICKS);
        message(player, "message.void_relic.returned", ChatFormatting.LIGHT_PURPLE);
        return InteractionResult.SUCCESS;
    }

    private static void message(Player player, String key, ChatFormatting color) {
        player.displayClientMessage(Component.translatable(key).withStyle(color), true);
    }
}
