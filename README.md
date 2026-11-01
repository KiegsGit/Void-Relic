# Void Relic

A Fabric mod for Minecraft Java 26.3 (Java 25).

## The item

**Void Relic** (epic rarity, stack size 1, no durability), found in the Tools & Utilities creative tab.

- **Rift Anchor**: right-click to save your location (purple spiral). Sneak + right-click to return to it.
  Same dimension only, the destination must be safe (room for the player, no lava/fire/cactus, solid ground or water within 4 blocks, inside the world border). 90 second cooldown, applied only after a successful teleport.
- **Void Magnet**: while held (main hand or off hand), dropped items and XP orbs within 8 blocks glide toward you with purple portal particles. Mobs and players are not affected.

## Recipe

```
A O A      A = Amethyst Shard
O E O      O = Obsidian
A P A      E = Echo Shard   P = Ender Pearl
```

The recipe unlocks in the recipe book when you pick up an Echo Shard.

## Installing

1. Install Minecraft Java 26.3 with Fabric Loader 0.19.5 or newer.
2. Put Fabric API (0.161.0+26.3 or newer for 26.3) and `void-relic-1.0.0.jar` in your `mods` folder.
3. Works in singleplayer and on servers (install on the server; clients need it too for the item texture and name).

## Building

Needs JDK 25.

```
gradle build
```

The jar is written to `build/libs/void-relic-1.0.0.jar` (ignore the `-sources` jar).

On GitHub, every push runs `.github/workflows/build.yml`. Download the jar from the run's Artifacts, or push a tag like `v1` to get it attached to a release.
