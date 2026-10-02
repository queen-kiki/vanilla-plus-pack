package dev.vanillaplus.tweaks;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

import java.util.Set;

/**
 * Tectonic stretches terrain upward, but biomes are still picked as if it weren't, so jungles end up at y350.
 * Above a treeline that rises with the climate's temperature we swap the biome for an alpine one: meadows
 * (or snowy slopes where it's frozen) first, then bare peaks. The biome sources call {@link #apply} on every
 * lookup, so terrain decoration, surface blocks, mob spawns and /locate all agree.
 */
@EventBusSubscriber(modid = VanillaPlusTweaks.MOD_ID)
public final class Alpine {
    // Vanilla climate temperature bands: below -0.45 is frozen, above 0.2 is warm
    private static final double FROZEN = -0.45;
    private static final double WARM = 0.2;
    private static final int JITTER = 12;
    private static final int JITTER_CELL = 32;

    private static volatile Settings settings;

    private Alpine() {
    }

    private record Settings(int coldY, int hotY, int peaksAbove, Holder<Biome> meadow, Holder<Biome> snowySlopes,
                            Holder<Biome> stonyPeaks, Holder<Biome> jaggedPeaks, Holder<Biome> frozenPeaks) {
        boolean isPeak(Holder<Biome> biome) {
            return biome == stonyPeaks || biome == jaggedPeaks || biome == frozenPeaks;
        }
    }

    /** Called from the biome source mixins with quart (4-block) coordinates. Safe to apply twice. */
    public static Holder<Biome> apply(Holder<Biome> biome, int quartX, int quartY, int quartZ, Climate.Sampler sampler) {
        Settings s = settings;
        int y = quartY << 2;
        if (s == null || y < s.coldY - JITTER) return biome;
        if (!biome.is(BiomeTags.IS_OVERWORLD) || biome.is(BiomeTags.IS_OCEAN) || biome.is(BiomeTags.IS_RIVER)) return biome;

        int x = quartX << 2;
        int z = quartZ << 2;
        double temperature = sampler.temperature().compute(new DensityFunction.SinglePointContext(x, y, z));
        double heat = Mth.clamp((temperature + 1) / 2, 0, 1);
        double treeline = Mth.lerp(heat, s.coldY, s.hotY) + jitter(x, z);
        if (y < treeline) return biome;

        if (y >= treeline + s.peaksAbove) {
            if (s.isPeak(biome)) return biome;
            return temperature < FROZEN ? s.frozenPeaks : temperature < WARM ? s.jaggedPeaks : s.stonyPeaks;
        }
        // Meadows, groves, cherry groves and slopes already belong up here
        if (biome.is(BiomeTags.IS_MOUNTAIN)) return biome;
        return temperature < FROZEN ? s.snowySlopes : s.meadow;
    }

    // Smooth value noise so the treeline wanders a little instead of cutting a flat contour
    private static double jitter(int x, int z) {
        int cx = Math.floorDiv(x, JITTER_CELL);
        int cz = Math.floorDiv(z, JITTER_CELL);
        double fx = smooth((x - cx * JITTER_CELL) / (double) JITTER_CELL);
        double fz = smooth((z - cz * JITTER_CELL) / (double) JITTER_CELL);
        double top = Mth.lerp(fx, corner(cx, cz), corner(cx + 1, cz));
        double bottom = Mth.lerp(fx, corner(cx, cz + 1), corner(cx + 1, cz + 1));
        return Mth.lerp(fz, top, bottom) * JITTER;
    }

    private static double smooth(double t) {
        return t * t * (3 - 2 * t);
    }

    // Deterministic pseudo-random value in [-1, 1] per grid corner
    private static double corner(int x, int z) {
        long h = Mth.getSeed(x, 0, z);
        return ((h >>> 11) & 0xFFFF) / 32767.5 - 1;
    }

    // Resolve the target biomes once the overworld exists, before its spawn chunks generate
    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level) || level.dimension() != Level.OVERWORLD) return;
        if (!Config.ALPINE_ENABLED.get()) {
            settings = null;
            return;
        }

        Registry<Biome> biomes = level.registryAccess().registryOrThrow(Registries.BIOME);
        Set<Holder<Biome>> possible = level.getChunkSource().getGenerator().getBiomeSource().possibleBiomes();
        Holder<Biome> meadow = target(biomes, possible, Biomes.MEADOW);
        Holder<Biome> snowySlopes = target(biomes, possible, Biomes.SNOWY_SLOPES);
        Holder<Biome> stonyPeaks = target(biomes, possible, Biomes.STONY_PEAKS);
        Holder<Biome> jaggedPeaks = target(biomes, possible, Biomes.JAGGED_PEAKS);
        Holder<Biome> frozenPeaks = target(biomes, possible, Biomes.FROZEN_PEAKS);
        // Decoration only knows the features of biomes the source can produce, so never hand out any others
        if (meadow == null || snowySlopes == null || stonyPeaks == null || jaggedPeaks == null || frozenPeaks == null) {
            VanillaPlusTweaks.LOGGER.warn("Alpine biomes disabled: the overworld can't generate all of meadow, snowy slopes and the peaks");
            settings = null;
            return;
        }
        settings = new Settings(Config.TREELINE_COLD_Y.get(), Config.TREELINE_HOT_Y.get(), Config.PEAKS_ABOVE_TREELINE.get(),
                meadow, snowySlopes, stonyPeaks, jaggedPeaks, frozenPeaks);
    }

    private static Holder<Biome> target(Registry<Biome> biomes, Set<Holder<Biome>> possible, ResourceKey<Biome> key) {
        return biomes.getHolder(key).filter(possible::contains).orElse(null);
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        settings = null;
    }
}
