package dev.vanillaplus.tweaks;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import dev.vanillaplus.tweaks.harvest.AutumnHarvestModifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.slf4j.Logger;

@Mod(VanillaPlusTweaks.MOD_ID)
public class VanillaPlusTweaks {
    public static final String MOD_ID = "vanillaplus_tweaks";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MOD_ID);

    static {
        LOOT_MODIFIERS.register("autumn_harvest", () -> AutumnHarvestModifier.CODEC);
    }

    public VanillaPlusTweaks(IEventBus modBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        LOOT_MODIFIERS.register(modBus);
        LOGGER.info("Vanilla Plus {} loaded", PackInfo.version());
    }
}
