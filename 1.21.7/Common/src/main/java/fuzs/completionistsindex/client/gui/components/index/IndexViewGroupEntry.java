package fuzs.completionistsindex.client.gui.components.index;

import fuzs.completionistsindex.client.gui.screens.index.IndexViewScreen;
import fuzs.completionistsindex.client.gui.screens.index.ItemsIndexViewScreen;
import fuzs.puzzleslib.api.client.gui.v2.GuiGraphicsHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class IndexViewGroupEntry extends IndexViewEntry {
    private final Component collection;
    private final float collectionProgress;
    private final List<ItemStack> items;

    public IndexViewGroupEntry(ItemStack item, Component displayName, boolean collected, List<Component> tooltipLines, Component collection, float collectionProgress, List<ItemStack> items) {
        super(item, displayName, collected, tooltipLines);
        this.collection = collection;
        this.collectionProgress = collectionProgress;
        this.items = items;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Comparable<? super T>> T toComparableKey() {
        return (T) this.displayName.getString();
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, int posX, int posY) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick, posX, posY);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED,
                IndexViewScreen.INDEX_LOCATION,
                posX + 24,
                posY + 11,
                140,
                198,
                91,
                5,
                512,
                256);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED,
                IndexViewScreen.INDEX_LOCATION,
                posX + 24,
                posY + 11,
                140,
                203,
                (int) (91 * this.collectionProgress),
                5,
                512,
                256);
        if (this.isMouseOver(mouseX, mouseY)) {
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

    @Override
    public void renderForeground(Minecraft minecraft, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, int posX, int posY) {
        super.renderForeground(minecraft, guiGraphics, mouseX, mouseY, partialTick, posX, posY);
        Font font = minecraft.font;
        guiGraphics.drawString(font,
                this.displayName,
                posX + 70 - font.width(this.displayName) / 2,
                posY,
                0xFF000000,
                false);
        GuiGraphicsHelper.drawInBatch8xOutline(guiGraphics,
                font,
                this.collection,
                posX + 70 - font.width(this.collection) / 2,
                posY + 10,
                0xFFFFC700,
                0xFF000000);
    }

    @Override
    public boolean mouseClicked(Screen screen, int mouseX, int mouseY, int buttonId) {
        screen.minecraft.setScreen(new ItemsIndexViewScreen(screen,
                ((IndexViewScreen<?>) screen).isFromInventory(),
                this.items));
        screen.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        return true;
    }
}
