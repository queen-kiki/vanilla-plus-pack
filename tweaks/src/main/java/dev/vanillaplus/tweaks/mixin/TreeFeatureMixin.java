package dev.vanillaplus.tweaks.mixin;

import dev.vanillaplus.tweaks.Alpine;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Trees that could grow past the treeline aren't placed, so tall ones thin out first and no crown pokes into the alpine biome. */
@Mixin(TreeFeature.class)
public abstract class TreeFeatureMixin {
    // Leaves can sit a few blocks above the trunk
    private static final int CROWN = 4;

    @Inject(method = "place", at = @At("HEAD"), cancellable = true)
    private void vanillaplus$treeline(FeaturePlaceContext<TreeConfiguration> context, CallbackInfoReturnable<Boolean> cir) {
        // Only during world generation: saplings players plant grow anywhere
        if (!(context.level() instanceof WorldGenRegion region)) return;
        ServerLevel level = region.getLevel();
        if (level.dimension() != Level.OVERWORLD) return;

        TrunkPlacerAccessor trunk = (TrunkPlacerAccessor) context.config().trunkPlacer;
        int maxHeight = trunk.vanillaplus$baseHeight() + trunk.vanillaplus$heightRandA() + trunk.vanillaplus$heightRandB() + CROWN;
        BlockPos origin = context.origin();
        if (!Alpine.treeFits(origin.getX(), origin.getY(), origin.getZ(), maxHeight,
                level.getChunkSource().randomState().sampler())) {
            cir.setReturnValue(false);
        }
    }
}
