package dev.vanillaplus.tweaks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/**
 * The big oceans mean you sometimes spawn in water. When that happens on your first join or on a
 * respawn without a bed, put a small raft on the surface, stand the player on it and hand them a boat.
 */
@EventBusSubscriber(modid = VanillaPlusTweaks.MOD_ID)
public final class SpawnRaft {
    private static final String JOINED_TAG = VanillaPlusTweaks.MOD_ID + ":joined";
    private static final ResourceLocation KAPOK_PLANKS =
            ResourceLocation.fromNamespaceAndPath("regions_unexplored", "kapok_planks");

    private SpawnRaft() {
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (player.getPersistentData().getBoolean(JOINED_TAG)) return;
        player.getPersistentData().putBoolean(JOINED_TAG, true);
        rescue(player);
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.isEndConquered() || !(event.getEntity() instanceof ServerPlayer player)) return;
        if (player.getRespawnPosition() != null) return; // bed or anchor: they chose that spot
        rescue(player);
    }

    private static void rescue(ServerPlayer player) {
        if (player.level().dimension() != Level.OVERWORLD || player.isSpectator()) return;
        ServerLevel level = player.serverLevel();
        BlockPos pos = player.blockPosition();
        if (!isWater(level, pos) && !isWater(level, pos.below())) return;

        // Find the top water block above (or just below) the player
        BlockPos surface = isWater(level, pos) ? pos : pos.below();
        while (isWater(level, surface.above()) && surface.getY() < level.getMaxBuildHeight() - 2) {
            surface = surface.above();
        }

        Block planks = BuiltInRegistries.BLOCK.getOptional(KAPOK_PLANKS).orElse(Blocks.OAK_PLANKS);
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos p = surface.offset(dx, 0, dz);
                if (isWater(level, p)) level.setBlockAndUpdate(p, planks.defaultBlockState());
            }
        }
        player.teleportTo(surface.getX() + 0.5, surface.getY() + 1, surface.getZ() + 0.5);

        boolean hasBoat = player.getInventory().items.stream().anyMatch(s -> s.is(ItemTags.BOATS));
        if (!hasBoat) player.getInventory().add(new ItemStack(Items.OAK_BOAT));
    }

    private static boolean isWater(ServerLevel level, BlockPos pos) {
        return level.getFluidState(pos).is(FluidTags.WATER);
    }
}
