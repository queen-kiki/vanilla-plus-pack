package dev.vanillaplus.tweaks.mixin;

import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TrunkPlacer.class)
public interface TrunkPlacerAccessor {
    @Accessor("baseHeight")
    int vanillaplus$baseHeight();

    @Accessor("heightRandA")
    int vanillaplus$heightRandA();

    @Accessor("heightRandB")
    int vanillaplus$heightRandB();
}
