package fuzs.completionistsindex.client.gui.screens.index;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.InputConstants;
import fuzs.completionistsindex.client.gui.components.index.IndexViewEntry;
import fuzs.completionistsindex.client.gui.components.index.IndexViewSingleEntry;
import fuzs.puzzleslib.api.client.gui.v2.components.SpritelessImageButton;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.ArrayList;
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
    protected Stream<IndexViewEntry> getPageEntries() {
        StatsCounter statsCounter = this.minecraft.player.getStats();
        return this.items.stream().map((ItemStack itemStack) -> {
            return createSingleEntry(itemStack, statsCounter, this.font);
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
                (Button button) -> {
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
        if (this.lastScreen != null) {
            this.lastScreen.onClose();
        }
    }

    private static IndexViewEntry createSingleEntry(ItemStack itemStack, StatsCounter statsCounter, Font font) {
        int pickedUp = statsCounter.getValue(Stats.ITEM_PICKED_UP, itemStack.getItem());
        int crafted = statsCounter.getValue(Stats.ITEM_CRAFTED, itemStack.getItem());
        boolean collected = pickedUp > 0 || crafted > 0;
        Component displayName = itemStack.getItem().getName(itemStack);
        Component formattedName = IndexViewPage.formatDisplayName(font, displayName, collected, true);
        List<Component> tooltipLines = new ArrayList<>();
        tooltipLines.add(Component.empty()
                .append(itemStack.getItem().getName(itemStack))
                .withStyle(itemStack.getRarity().color()));
        TooltipDisplay tooltipDisplay = itemStack.getOrDefault(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT);
        itemStack.addDetailsToTooltip(Item.TooltipContext.EMPTY,
                tooltipDisplay,
                null,
                TooltipFlag.NORMAL,
                tooltipLines::add);
        if (pickedUp > 0) {
            tooltipLines.add(Component.literal(String.valueOf(pickedUp))
                    .append(" ")
                    .append(Component.translatable("stat_type.minecraft.picked_up"))
                    .withStyle(ChatFormatting.BLUE));
        }
        if (crafted > 0) {
            tooltipLines.add(Component.literal(String.valueOf(crafted))
                    .append(" ")
                    .append(Component.translatable("stat_type.minecraft.crafted"))
                    .withStyle(ChatFormatting.BLUE));
        }
        return new IndexViewSingleEntry(itemStack, formattedName, collected, ImmutableList.copyOf(tooltipLines));
    }
}
