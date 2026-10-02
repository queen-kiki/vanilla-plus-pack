package dev.vanillaplus.tweaks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;

/** Everything that touches Serene Seasons goes through here, so the mod still works without it. */
public final class Seasons {
    private static final boolean LOADED = ModList.get().isLoaded("sereneseasons");

    private Seasons() {
    }

    /**
     * Air pressure multiplier for airships: 1 + lift in mid-winter, 1 - lift in mid-summer,
     * following a smooth curve over the twelve sub-seasons.
     */
    public static double liftFactor(Level level) {
        double lift = Config.SEASONAL_LIFT.get();
        if (!LOADED || lift == 0) return 1.0;
        int sub = SeasonHelper.getSeasonState(level).getSubSeason().ordinal(); // 0 = early spring ... 10 = mid winter
        return 1.0 + lift * Math.cos(2 * Math.PI * (sub - 10) / 12.0);
    }

    public static boolean isAutumnAt(Level level, BlockPos pos) {
        if (!LOADED) return false;
        if (SeasonHelper.usesTropicalSeasons(level.getBiome(pos))) return false;
        return SeasonHelper.getSeasonState(level).getSeason() == Season.AUTUMN;
    }
}
