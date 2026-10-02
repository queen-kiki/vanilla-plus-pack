package dev.vanillaplus.tweaks.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import org.spongepowered.asm.mixin.Mixin;

/**
 * MidnightLib keeps every mod's config entries in one static map, so two mods calling init at the
 * same time during parallel mod loading crash with a ConcurrentModificationException. Serialize init.
 * (MidnightLib is bundled inside Countered's mods, hence the string target.)
 */
@Mixin(targets = "eu.midnightdust.lib.config.MidnightConfig", remap = false)
public abstract class MidnightConfigMixin {
    private static final Object VANILLAPLUS$LOCK = new Object();

    @WrapMethod(method = "init", remap = false)
    private static void vanillaplus$serializeInit(String modid, Class<?> config, Operation<Void> original) {
        synchronized (VANILLAPLUS$LOCK) {
            original.call(modid, config);
        }
    }
}
