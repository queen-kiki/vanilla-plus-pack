package dev.vanillaplus.tweaks.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.vanillaplus.tweaks.Alpine;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Alpine biomes above the treeline. */
@Mixin(MultiNoiseBiomeSource.class)
public abstract class MultiNoiseBiomeSourceMixin {
    @ModifyReturnValue(method = "getNoiseBiome(IIILnet/minecraft/world/level/biome/Climate$Sampler;)Lnet/minecraft/core/Holder;", at = @At("RETURN"))
    private Holder<Biome> vanillaplus$alpine(Holder<Biome> biome, int x, int y, int z, Climate.Sampler sampler) {
        return Alpine.apply(biome, x, y, z, sampler);
    }
}
