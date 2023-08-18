package fuzs.completionistsindex.client.gui.screens.inventory;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ItemsIndexViewScreen extends IndexViewScreen {
    private final List<ItemStack> items;

    public ItemsIndexViewScreen(Screen lastScreen, List<ItemStack> items) {
        super(lastScreen);
        this.items = items;
    }

    @Override
    protected List<IndexViewPage.Entry> getPageEntries() {
        StatsCounter stats = this.minecraft.player.getStats();
        return this.items.stream().map(stack -> {
            return IndexViewPage.statsItemEntry(stack, stats, this.font);
        }).sorted(this.getComparator()).toList();
    }

    @Override
    protected void init() {
        super.init();
        this.rebuildPages();
    }
}
