package fuzs.completionistsindex.client.gui.screens.inventory;

import com.mojang.blaze3d.platform.InputConstants;
import fuzs.puzzleslib.api.client.gui.v2.components.SpritelessImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.stream.Stream;

public class ItemsIndexViewScreen extends IndexViewScreen<StatsSorting> {
    private static StatsSorting statsSorting = StatsSorting.CREATIVE;
    private final List<ItemStack> items;

    public ItemsIndexViewScreen(Screen lastScreen, boolean fromInventory, List<ItemStack> items) {
        super(lastScreen, fromInventory);
        this.items = items;
    }

    @Override
    protected Stream<IndexViewPage.Entry> getPageEntries() {
        StatsCounter statsCounter = this.minecraft.player.getStats();
        return this.items.stream().map((ItemStack itemStack) -> {
            return IndexViewPage.createSingleEntry(itemStack, statsCounter, this.font);
        });
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(new SpritelessImageButton(this.leftPos + 17,
                this.topPos + 11,
                16,
                13,
                42,
                202,
                20,
                INDEX_LOCATION,
                512,
                256,
                button -> {
                    this.minecraft.setScreen(this.lastScreen);
                })).setTooltip(Tooltip.create(CommonComponents.GUI_BACK));
        this.rebuildPages();
    }

    @Override
    protected StatsSorting getSortProvider() {
        return statsSorting;
    }

    @Override
    protected void setSortProvider(StatsSorting sortProvider) {
        statsSorting = sortProvider;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        } else if (keyCode == InputConstants.KEY_BACKSPACE && this.shouldCloseOnEsc()) {
            this.minecraft.setScreen(this.lastScreen);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onClose() {
        this.lastScreen.onClose();
    }
}
