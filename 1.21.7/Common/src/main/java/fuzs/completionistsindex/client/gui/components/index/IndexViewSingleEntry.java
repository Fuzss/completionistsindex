package fuzs.completionistsindex.client.gui.components.index;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class IndexViewSingleEntry extends IndexViewEntry {

    public IndexViewSingleEntry(ItemStack item, Component displayName, boolean collected, List<Component> tooltipLines) {
        super(item, displayName, collected, tooltipLines);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Comparable<? super T>> T toComparableKey() {
        return (T) BuiltInRegistries.ITEM.getKey(this.item.getItem()).getPath();
    }

    @Override
    public void renderForeground(Minecraft minecraft, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, int posX, int posY) {
        super.renderForeground(minecraft, guiGraphics, mouseX, mouseY, partialTick, posX, posY);
        renderScrollingString(guiGraphics,
                minecraft.font,
                this.displayName,
                posX + 23,
                posY + 4,
                posX + 23 + 95,
                posY + 4 + minecraft.font.lineHeight,
                0xFF000000);
    }

    /**
     * Copied from
     * {@link net.minecraft.client.gui.components.AbstractWidget#renderScrollingString(GuiGraphics, Font, Component,
     * int, int, int, int, int)}.
     * <p>
     * Allows for rendering without enabled {@code dropShadow}.
     */
    protected static void renderScrollingString(GuiGraphics guiGraphics, Font font, Component text, int minX, int minY, int maxX, int maxY, int color) {
        renderScrollingString(guiGraphics, font, text, (minX + maxX) / 2, minX, minY, maxX, maxY, color);
    }

    /**
     * Copied from
     * {@link net.minecraft.client.gui.components.AbstractWidget#renderScrollingString(GuiGraphics, Font, Component,
     * int, int, int, int, int, int)}.
     * <p>
     * Allows for rendering without enabled {@code dropShadow}.
     */
    protected static void renderScrollingString(GuiGraphics guiGraphics, Font font, Component text, int centerX, int minX, int minY, int maxX, int maxY, int color) {
        int i = font.width(text);
        int j = (minY + maxY - 9) / 2 + 1;
        int k = maxX - minX;
        if (i > k) {
            int l = i - k;
            double d = (double) Util.getMillis() / 1000.0;
            double e = Math.max((double) l * 0.5, 3.0);
            double f = Math.sin((Math.PI / 2) * Math.cos((Math.PI * 2) * d / e)) / 2.0 + 0.5;
            double g = Mth.lerp(f, 0.0, l);
            guiGraphics.enableScissor(minX, minY, maxX, maxY);
            guiGraphics.drawString(font, text, minX - (int) g, j, color, false);
            guiGraphics.disableScissor();
        } else {
            int l = Mth.clamp(centerX, minX + i / 2, maxX - i / 2);
            FormattedCharSequence formattedCharSequence = text.getVisualOrderText();
            guiGraphics.drawString(font, text, l - font.width(formattedCharSequence) / 2, j, color, false);
        }
    }
}
