package dev.vanillaplus.tweaks;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingBreatheEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

/**
 * Thin air high up in the overworld. We only mark the player as unable to breathe; vanilla then drains
 * the air bar and deals damage, and Create's diving helmet (which listens to the same event after us)
 * supplies backtank air exactly like it does underwater.
 */
@EventBusSubscriber(modid = VanillaPlusTweaks.MOD_ID)
public final class Oxygen {
    public static final ResourceKey<DamageType> THIN_AIR =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(VanillaPlusTweaks.MOD_ID, "thin_air"));

    private Oxygen() {
    }

    public static boolean isThinAir(LivingEntity entity) {
        return Config.OXYGEN_ENABLED.get()
                && entity.level().dimension() == Level.OVERWORLD
                && entity.getY() >= Config.OXYGEN_START_Y.get();
    }

    // HIGH so we run before Create's diving helmet, which only steps in when breathing is blocked
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onBreathe(LivingBreatheEvent event) {
        if (!(event.getEntity() instanceof Player player) || player.getAbilities().invulnerable) return;
        if (!isThinAir(player)) return;

        event.setCanBreathe(false);
        boolean fullRate = player.getY() >= Config.OXYGEN_FULL_RATE_Y.get();
        // Below fullRateY the air drains at half speed
        event.setConsumeAirAmount(fullRate || player.tickCount % 2 == 0 ? 1 : 0);
    }

    // Vanilla calls it drowning; up here it isn't, so swap in our own damage type and death message
    @SubscribeEvent
    public static void onDamage(LivingIncomingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();
        if (!source.is(DamageTypes.DROWN) || entity.isEyeInFluid(FluidTags.WATER) || !isThinAir(entity)) return;

        event.setCanceled(true);
        entity.hurt(entity.damageSources().source(THIN_AIR), event.getAmount());
    }
}
