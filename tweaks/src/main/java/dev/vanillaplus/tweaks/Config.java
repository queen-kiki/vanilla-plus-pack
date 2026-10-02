package dev.vanillaplus.tweaks;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    static {
        BUILDER.push("oxygen");
    }

    public static final ModConfigSpec.BooleanValue OXYGEN_ENABLED = BUILDER
            .comment("Air runs out high up in the overworld. A Create diving helmet with a filled backtank supplies air.")
            .define("enabled", true);
    public static final ModConfigSpec.IntValue OXYGEN_START_Y = BUILDER
            .comment("Height where the air starts getting thin.")
            .defineInRange("startY", 450, -64, 2048);
    public static final ModConfigSpec.IntValue OXYGEN_FULL_RATE_Y = BUILDER
            .comment("Height where air runs out at the full underwater rate. Between startY and this it drains at half speed.")
            .defineInRange("fullRateY", 550, -64, 2048);

    static {
        BUILDER.pop().push("alpine");
    }

    public static final ModConfigSpec.BooleanValue ALPINE_ENABLED = BUILDER
            .comment("High up in the overworld, biomes turn alpine: meadows and snowy slopes above the treeline, bare peaks above that.",
                    "Only affects newly generated chunks.")
            .define("enabled", true);
    public static final ModConfigSpec.IntValue TREELINE_COLD_Y = BUILDER
            .comment("Treeline in the coldest climates.")
            .defineInRange("treelineColdY", 190, -64, 2048);
    public static final ModConfigSpec.IntValue TREELINE_HOT_Y = BUILDER
            .comment("Treeline in the hottest climates. Climates in between get a height in between.")
            .defineInRange("treelineHotY", 330, -64, 2048);
    public static final ModConfigSpec.IntValue PEAKS_ABOVE_TREELINE = BUILDER
            .comment("How far above the treeline the bare peaks start.")
            .defineInRange("peaksAboveTreeline", 48, 0, 1024);

    static {
        BUILDER.pop().push("airships");
    }

    public static final ModConfigSpec.DoubleValue SEASONAL_LIFT = BUILDER
            .comment("How much the seasons change air pressure for airships. Cold air is denser, so winter gives more lift.",
                    "0.08 means +8% in mid-winter and -8% in mid-summer. 0 turns it off.")
            .defineInRange("seasonalLift", 0.08, 0.0, 0.5);

    static {
        BUILDER.pop().push("harvest");
    }

    public static final ModConfigSpec.DoubleValue AUTUMN_BONUS_CHANCE = BUILDER
            .comment("Chance per harvested crop item to drop one extra in autumn.")
            .defineInRange("autumnBonusChance", 0.5, 0.0, 1.0);

    static {
        BUILDER.pop().push("hud");
    }

    public static final ModConfigSpec.IntValue ALTITUDE_HUD_Y = BUILDER
            .comment("Show the altitude readout at or above this height. Set very high to hide it.")
            .defineInRange("altitudeFromY", 200, -64, 4096);

    static {
        BUILDER.pop();
    }

    public static final ModConfigSpec SPEC = BUILDER.build();

    private Config() {
    }
}
