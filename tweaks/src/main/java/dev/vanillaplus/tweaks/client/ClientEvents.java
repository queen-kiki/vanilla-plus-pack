package dev.vanillaplus.tweaks.client;

import dev.vanillaplus.tweaks.Config;
import dev.vanillaplus.tweaks.PackInfo;
import dev.vanillaplus.tweaks.VanillaPlusTweaks;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;

@EventBusSubscriber(modid = VanillaPlusTweaks.MOD_ID, value = Dist.CLIENT)
public final class ClientEvents {
    private static final int CLOUD_Y = 320; // matches the Complementary cloud height the pack ships

    private ClientEvents() {
    }

    @SubscribeEvent
    public static void onTitleScreen(ScreenEvent.Render.Post event) {
        if (event.getScreen() instanceof TitleScreen) {
            event.getGuiGraphics().drawString(Minecraft.getInstance().font, "Vanilla Plus " + PackInfo.version(), 2, 2, 0xFFFFFF);
        }
    }

    @SubscribeEvent
    public static void onDebugText(CustomizeGuiOverlayEvent.DebugText event) {
        event.getLeft().add("Vanilla Plus " + PackInfo.version());
    }

    private static void renderAltitude(GuiGraphics graphics, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui || mc.getDebugOverlay().showDebugScreen()) return;
        double y = mc.player.getY();
        if (y < Config.ALTITUDE_HUD_Y.get()) return;

        String note = null;
        int noteColor = 0;
        if (Config.OXYGEN_ENABLED.get() && y >= Config.OXYGEN_START_Y.get()) {
            note = "thin air";
            noteColor = 0xFFAA55;
        } else if (y >= CLOUD_Y) {
            note = "above the clouds";
            noteColor = 0xAADDFF;
        }

        Font font = mc.font;
        String altitude = "Y " + (int) Math.floor(y);
        int center = graphics.guiWidth() / 2;
        graphics.drawString(font, altitude, center - font.width(altitude) / 2, 4, 0xFFFFFF);
        if (note != null) {
            graphics.drawString(font, note, center - font.width(note) / 2, 14, noteColor);
        }
    }

    @SubscribeEvent
    public static void registerLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(ResourceLocation.fromNamespaceAndPath(VanillaPlusTweaks.MOD_ID, "altitude"),
                ClientEvents::renderAltitude);
    }
}
