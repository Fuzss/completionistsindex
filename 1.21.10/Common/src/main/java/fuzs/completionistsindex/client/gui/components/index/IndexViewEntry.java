package fuzs.completionistsindex.client.gui.components.index;

import com.google.common.collect.ImmutableList;
import fuzs.completionistsindex.client.gui.screens.index.IndexViewScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.stats.StatsCounter;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

import java.util.List;
import java.util.Optional;

public abstract class IndexViewEntry<S extends IndexViewScreen<?>> {
    protected static final List<StatType<Item>> RELEVANT_STAT_TYPES = ImmutableList.of(Stats.ITEM_PICKED_UP,
            Stats.ITEM_CRAFTED);
    private static final ResourceLocation SLOT_HIGHLIGHT_BACK_SPRITE = ResourceLocation.withDefaultNamespace(
            "container/slot_highlight_back");
    private static final ResourceLocation SLOT_HIGHLIGHT_FRONT_SPRITE = ResourceLocation.withDefaultNamespace(
            "container/slot_highlight_front");

    protected final S screen;
    private final ItemStack itemStack;
    private final Component displayName;
    private List<Component> tooltipLines;

    public IndexViewEntry(S screen, ItemStack itemStack, Component displayName) {
        this.screen = screen;
        this.itemStack = itemStack;
        this.displayName = displayName;
    }

    @MustBeInvokedByOverriders
    public void initialize(StatsCounter statsCounter) {
        this.tooltipLines = this.createTooltipLines(this.itemStack, statsCounter);
    }

    protected abstract List<Component> createTooltipLines(ItemStack itemStack, StatsCounter statsCounter);

    public abstract String toComparableKey();

    protected Item getItem() {
        return this.itemStack.getItem();
    }

    public Component getDisplayName() {
        return this.displayName;
    }

    public String getDisplayNameString() {
        return this.getDisplayName().getString();
    }

    protected Component getStyledDisplayName() {
        return Component.empty().append(this.getDisplayName()).withStyle(this.getTitleStyle());
    }

    private Style getTitleStyle() {
        return this.isCollected() ? Style.EMPTY.withColor(ARGB.opaque(0x4BA52F)) :
                Style.EMPTY.withColor(ChatFormatting.BLACK);
    }

    public abstract boolean isCollected();

    protected int getStatsValue(StatsCounter statsCounter, Item item) {
        return RELEVANT_STAT_TYPES.stream()
                .mapToInt((StatType<Item> statType) -> statsCounter.getValue(statType, item))
                .sum();
    }

    public final void renderWithTooltip(Font font, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, int posX, int posY) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick, posX, posY);
        this.renderForeground(guiGraphics, mouseX, mouseY, partialTick, posX, posY, font);
        if (this.isHoveringSlot(posX, posY, mouseX, mouseY)) {
            guiGraphics.setTooltipForNextFrame(font, this.tooltipLines, Optional.empty(), mouseX, mouseY);
        }
    }

    public boolean isHoveringSlot(int posX, int posY, int mouseX, int mouseY) {
        return this.isHovering(posX, posY, posX + 16, posY + 16, mouseX, mouseY);
    }

    public boolean isMouseOver(int posX, int posY, int mouseX, int mouseY) {
        return this.isHovering(posX, posY, posX + 134, posY + 18, mouseX, mouseY);
    }

    private boolean isHovering(int minX, int minY, int maxX, int maxY, int mouseX, int mouseY) {
        return mouseX > minX && mouseX <= maxX && mouseY > minY && mouseY <= maxY;
    }

    public abstract boolean mouseClicked(MouseButtonEvent mouseButtonEvent);

    protected void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, int posX, int posY) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED,
                IndexViewScreen.INDEX_LOCATION,
                posX,
                posY,
                120,
                208,
                18,
                18,
                512,
                256);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED,
                IndexViewScreen.INDEX_LOCATION,
                posX + 124,
                posY + 4,
                120 + (this.isCollected() ? 10 : 0),
                198,
                10,
                10,
                512,
                256);
        if (this.isClickable() && this.isMouseOver(posX, posY, mouseX, mouseY)) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED,
                    IndexViewScreen.INDEX_LOCATION,
                    posX - 2,
                    posY - 2,
                    316,
                    0,
                    140,
                    22,
                    512,
                    256);
        }
    }

    protected abstract boolean isClickable();

    protected void renderForeground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, int posX, int posY, Font font) {
        if (this.isHoveringSlot(posX, posY, mouseX, mouseY)) {
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED,
                    SLOT_HIGHLIGHT_BACK_SPRITE,
                    posX + 1 - 4,
                    posY + 1 - 4,
                    24,
                    24);
        }
        guiGraphics.renderItem(this.itemStack, posX + 1, posY + 1);
        if (this.isHoveringSlot(posX, posY, mouseX, mouseY)) {
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED,
                    SLOT_HIGHLIGHT_FRONT_SPRITE,
                    posX + 1 - 4,
                    posY + 1 - 4,
                    24,
                    24);
        }
        renderScrollingString(guiGraphics,
                font,
                this.getStyledDisplayName(),
                posX + 23,
                posY + this.getDisplayNameYOffset(),
                posX + 23 + 95,
                posY + this.getDisplayNameYOffset() + font.lineHeight,
                ARGB.opaque(0));
    }

    protected abstract int getDisplayNameYOffset();

    /**
     * Allows for rendering without enabled {@code dropShadow}.
     *
     * @see net.minecraft.client.gui.components.AbstractWidget#renderScrollingString(GuiGraphics, Font, Component,
     *         int, int, int, int, int)
     */
    protected static void renderScrollingString(GuiGraphics guiGraphics, Font font, Component text, int minX, int minY, int maxX, int maxY, int color) {
        renderScrollingString(guiGraphics, font, text, (minX + maxX) / 2, minX, minY, maxX, maxY, color);
    }

    /**
     * Allows for rendering without enabled {@code dropShadow}.
     *
     * @see net.minecraft.client.gui.components.AbstractWidget#renderScrollingString(GuiGraphics, Font, Component,
     *         int, int, int, int, int, int)
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
