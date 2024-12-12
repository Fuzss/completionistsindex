package fuzs.completionistsindex.client.gui.screens.inventory;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ItemsIndexViewScreen extends IndexViewScreen {
    private static StatsSorting statsSorting = StatsSorting.CREATIVE;
    private final List<ItemStack> items;

    public ItemsIndexViewScreen(Screen lastScreen, boolean fromInventory, List<ItemStack> items) {
        super(lastScreen, fromInventory);
        this.items = items;
    }

    @Override
    protected List<IndexViewPage.Entry> getPageEntries() {
        StatsCounter statsCounter = this.minecraft.player.getStats();
        return this.items.stream().map(stack -> {
            return IndexViewPage.createSingleEntry(stack, statsCounter, this.font);
        }).sorted(statsSorting.getComparator()).toList();
    }

    @Override
    protected void init() {
        super.init();
        this.rebuildPages();
    }

    @Override
    protected void cyclePageContents() {
        statsSorting = statsSorting.cycle();
    }

    @Override
    protected Component getTooltipComponent() {
        return statsSorting.getComponent();
    }
}
