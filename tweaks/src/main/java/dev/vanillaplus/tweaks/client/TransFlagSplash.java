package dev.vanillaplus.tweaks.client;

import com.mojang.math.Axis;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/** The title screen splash, always: the trans flag glyph from Estrogen's flag font. */
public class TransFlagSplash extends SplashRenderer {
    public static final TransFlagSplash INSTANCE = new TransFlagSplash();

    private static final Component FLAG = Component.literal("")
            .withStyle(Style.EMPTY.withFont(ResourceLocation.fromNamespaceAndPath("estrogen", "flags")));

    private TransFlagSplash() {
        super("");
    }

    // Same placement and pulse as vanilla's splash text, but drawn white so the flag keeps its colours
    @Override
    public void render(GuiGraphics graphics, int screenWidth, Font font, int color) {
        graphics.pose().pushPose();
        graphics.pose().translate(screenWidth / 2.0F + 123.0F, 69.0F, 0.0F);
        graphics.pose().mulPose(Axis.ZP.rotationDegrees(-20.0F));
        float scale = 1.8F - Mth.abs(Mth.sin((float) (Util.getMillis() % 1000L) / 1000.0F * Mth.TWO_PI) * 0.1F);
        scale = scale * 100.0F / (font.width(FLAG) + 32);
        graphics.pose().scale(scale, scale, scale);
        graphics.drawCenteredString(font, FLAG, 0, -8, 0xFFFFFF | color);
        graphics.pose().popPose();
    }
}
