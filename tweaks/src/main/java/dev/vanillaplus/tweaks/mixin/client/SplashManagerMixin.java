package dev.vanillaplus.tweaks.mixin.client;

import dev.vanillaplus.tweaks.client.TransFlagSplash;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.client.resources.SplashManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SplashManager.class)
public abstract class SplashManagerMixin {
    @Inject(method = "getSplash", at = @At("HEAD"), cancellable = true)
    private void vanillaplus$alwaysTransFlag(CallbackInfoReturnable<SplashRenderer> cir) {
        cir.setReturnValue(TransFlagSplash.INSTANCE);
    }
}
