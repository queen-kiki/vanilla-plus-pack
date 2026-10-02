package dev.vanillaplus.tweaks.harvest;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.vanillaplus.tweaks.Config;
import dev.vanillaplus.tweaks.Seasons;
import dev.vanillaplus.tweaks.VanillaPlusTweaks;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.loot.LootModifier;

/** In autumn, harvested crops sometimes drop one extra. Which items count is the tag below. */
public class AutumnHarvestModifier extends LootModifier {
    public static final MapCodec<AutumnHarvestModifier> CODEC =
            RecordCodecBuilder.mapCodec(inst -> codecStart(inst).apply(inst, AutumnHarvestModifier::new));

    private static final TagKey<Item> BONUS_ITEMS =
            TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(VanillaPlusTweaks.MOD_ID, "autumn_harvest"));

    public AutumnHarvestModifier(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> loot, LootContext context) {
        Vec3 origin = context.getParamOrNull(LootContextParams.ORIGIN);
        if (context.getParamOrNull(LootContextParams.BLOCK_STATE) == null || origin == null) return loot;
        if (!Seasons.isAutumnAt(context.getLevel(), BlockPos.containing(origin))) return loot;

        double chance = Config.AUTUMN_BONUS_CHANCE.get();
        for (ItemStack stack : loot) {
            if (stack.is(BONUS_ITEMS) && context.getRandom().nextDouble() < chance) {
                stack.grow(1);
            }
        }
        return loot;
    }

    @Override
    public MapCodec<? extends LootModifier> codec() {
        return CODEC;
    }
}
