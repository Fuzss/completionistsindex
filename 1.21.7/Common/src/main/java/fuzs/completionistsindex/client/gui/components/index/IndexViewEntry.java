package fuzs.completionistsindex.client.gui.components.index;

import fuzs.completionistsindex.client.gui.screens.index.IndexViewScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public abstract class IndexViewEntry {
    protected final ItemStack item;
    protected final Component displayName;
    private final boolean collected;
    private final List<Component> tooltipLines;

    public IndexViewEntry(ItemStack item, Component displayName, boolean collected, List<Component> tooltipLines) {
        this.item = item;
        this.displayName = displayName;
        this.collected = collected;
        this.tooltipLines = tooltipLines;
    }

    public abstract <T extends Comparable<? super T>> T toComparableKey();

    public String getString() {
        return this.displayName.getString();
    }

    public boolean isCollected() {
        return this.collected;
    }

    public List<Component> getTooltipLines() {
        return this.tooltipLines;
    }

    public void render(Minecraft minecraft, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, int posX, int posY) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick, posX, posY);
        this.renderForeground(minecraft, guiGraphics, mouseX, mouseY, partialTick, posX, posY);
    }

    public boolean isHoveringSlot(int mouseX, int mouseY) {
        return this.isHovering(0, 0, 16, 16, mouseX, mouseY);
    }

    public boolean isMouseOver(int mouseX, int mouseY) {
        return this.isHovering(0, 0, 134, 18, mouseX, mouseY);
    }

    public boolean mouseClicked(Screen screen, int mouseX, int mouseY, int buttonId) {
        return false;
    }

    private boolean isHovering(int minX, int minY, int maxX, int maxY, int mouseX, int mouseY) {
        return mouseX > minX && mouseX <= maxX && mouseY > minY && mouseY <= maxY;
    }

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
                120 + (this.collected ? 10 : 0),
                198,
                10,
                10,
                512,
                256);
    }

    protected void renderForeground(Minecraft minecraft, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, int posX, int posY) {
        guiGraphics.renderItem(this.item, posX + 1, posY + 1);
    }
}
