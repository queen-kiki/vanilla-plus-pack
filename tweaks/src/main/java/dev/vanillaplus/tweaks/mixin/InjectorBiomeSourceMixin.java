package dev.vanillaplus.tweaks.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.vanillaplus.tweaks.Alpine;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Lithostitched wraps the overworld biome source to inject Regions Unexplored's biomes after the vanilla
 * lookup, so the alpine swap has to run on its result too.
 */
@Pseudo
@Mixin(targets = "dev.worldgen.lithostitched.impl.worldgen.biomeinjector.internal.InjectorBiomeSource", remap = false)
public abstract class InjectorBiomeSourceMixin {
    @ModifyReturnValue(method = "getNoiseBiome", at = @At("RETURN"), remap = false)
    private Holder<Biome> vanillaplus$alpine(Holder<Biome> biome, int x, int y, int z, Climate.Sampler sampler) {
        return Alpine.apply(biome, x, y, z, sampler);
    }
}
