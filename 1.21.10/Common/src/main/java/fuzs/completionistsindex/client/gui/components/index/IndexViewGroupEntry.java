package fuzs.completionistsindex.client.gui.components.index;

import com.google.common.collect.ImmutableList;
import fuzs.completionistsindex.client.gui.screens.index.IndexViewScreen;
import fuzs.completionistsindex.client.gui.screens.index.ItemsIndexViewScreen;
import fuzs.completionistsindex.client.gui.screens.index.ModsIndexViewScreen;
import fuzs.puzzleslib.api.client.gui.v2.GuiGraphicsHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.StatsCounter;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.text.DecimalFormat;
import java.util.List;

public class IndexViewGroupEntry extends IndexViewEntry<ModsIndexViewScreen> {
    private static final DecimalFormat PERCENTAGE_FORMAT = new DecimalFormat("#.##");

    private final List<ItemStack> items;
    private int collectedItems;

    public IndexViewGroupEntry(ModsIndexViewScreen screen, List<ItemStack> items, Component displayName) {
        super(screen, Util.getRandom(items, IndexViewScreen.RANDOM), displayName);
        this.items = items;
    }

    @Override
    public void initialize(StatsCounter statsCounter) {
        super.initialize(statsCounter);
        this.collectedItems = this.getCollectedItemsValue(statsCounter);
    }

    private int getCollectedItemsValue(StatsCounter statsCounter) {
        return (int) this.items.stream().map(ItemStack::getItem).mapToInt((Item item) -> {
            return this.getStatsValue(statsCounter, item);
        }).filter((int value) -> {
            return value > 0;
        }).count();
    }

    @Override
    protected List<Component> createTooltipLines(ItemStack itemStack, StatsCounter statsCounter) {
        float progressAmount = this.getProgressAmount(this.getCollectedItemsValue(statsCounter));
        Component component = Component.literal(" (" + PERCENTAGE_FORMAT.format(progressAmount * 100.0F) + "%)")
                .withStyle(ChatFormatting.GOLD);
        return ImmutableList.of(Component.empty().append(this.getDisplayName()).append(component));
    }

    private float getProgressAmount(int collectedItemsValue) {
        return collectedItemsValue / (float) this.items.size();
    }

    private Component getProgressComponent(int collectedItemsValue) {
        return Component.literal(collectedItemsValue + "/" + this.items.size());
    }

    @Override
    public String toComparableKey() {
        return this.getDisplayNameString();
    }

    @Override
    public boolean isCollected() {
        return this.collectedItems == this.items.size();
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
                (int) (91 * this.getProgressAmount(this.collectedItems)),
                5,
                512,
                256);
    }

    @Override
    protected boolean isClickable() {
        return true;
    }

    @Override
    public void renderForeground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, int posX, int posY, Font font) {
        super.renderForeground(guiGraphics, mouseX, mouseY, partialTick, posX, posY, font);
        Component progressComponent = this.getProgressComponent(this.collectedItems);
        GuiGraphicsHelper.drawInBatch8xOutline(guiGraphics,
                font,
                progressComponent,
                posX + 70 - font.width(progressComponent) / 2,
                posY + 10,
                ARGB.opaque(0xFFC700),
                ARGB.opaque(0));
    }

    @Override
    protected int getDisplayNameYOffset() {
        return 0;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent) {
        this.screen.minecraft.setScreen(new ItemsIndexViewScreen(this.screen,
                this.screen.isFromInventory(),
                this.items));
        this.screen.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        return true;
    }
}
