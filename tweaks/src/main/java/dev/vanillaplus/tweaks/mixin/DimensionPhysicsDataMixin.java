package dev.vanillaplus.tweaks.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.ryanhcode.sable.physics.config.dimension_physics.DimensionPhysicsData;
import dev.vanillaplus.tweaks.Seasons;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Cold air is denser: scale Sable's air pressure (and so airship lift) with the season. */
@Mixin(value = DimensionPhysicsData.class, remap = false)
public abstract class DimensionPhysicsDataMixin {
    @ModifyReturnValue(method = "getAirPressure", at = @At("RETURN"), remap = false)
    private static double vanillaplus$seasonalPressure(double pressure, @Local(argsOnly = true) Level level) {
        return level.dimension() == Level.OVERWORLD ? pressure * Seasons.liftFactor(level) : pressure;
    }
}
