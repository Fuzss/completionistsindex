package fuzs.completionistsindex.client.gui.screens.inventory;

import com.google.common.collect.Maps;
import fuzs.completionistsindex.CompletionistsIndex;
import fuzs.completionistsindex.config.ClientConfig;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.achievement.StatsUpdateListener;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ModsIndexViewScreen extends IndexViewScreen implements StatsUpdateListener {
    private static final Component DOWNLOAD_PENDING_COMPONENT = Component.translatable("multiplayer.downloadingStats");
    public static final String ALL_ITEMS_PLACEHOLDER = "__ALL__";

    private final Map<String, List<ItemStack>> allItemsByMod = getAllItemsByMod();
    private boolean isLoading = true;

    public ModsIndexViewScreen(Screen lastScreen) {
        super(lastScreen);
    }

    private static Map<String, List<ItemStack>> getAllItemsByMod() {
        Minecraft minecraft = Minecraft.getInstance();
        // always set hasPermissions to false, we do not want unobtainable items in the index
        CreativeModeTabs.tryRebuildTabContents(minecraft.player.connection.enabledFeatures(), false, minecraft.level.registryAccess());
        Collection<ItemStack> displayItems = CreativeModeTabs.searchTab().getDisplayItems();
        List<ItemStack> items = displayItems.stream()
                .map(ItemStack::getItem)
                .distinct()
                .filter(Predicate.not(CompletionistsIndex.CONFIG.get(ClientConfig.class).blacklist::contains))
                .map(ItemStack::new)
                .collect(Collectors.toList());
        // use linked hash map to maybe preserve mod order
        Map<String, List<ItemStack>> displayItemsByMod = items.stream()
                .collect(Collectors.groupingBy(item -> BuiltInRegistries.ITEM.getKey(item.getItem()).getNamespace(), Maps::newLinkedHashMap, Collectors.toList()));
        if (displayItemsByMod.size() > 1) displayItemsByMod.put(ALL_ITEMS_PLACEHOLDER, items);
        return displayItemsByMod;
    }

    @Override
    public void onStatsUpdated() {
        if (this.isLoading) {
            this.isLoading = false;
            this.rebuildPages();
        }
    }

    @Override
    protected void rebuildPages() {
        if (!this.isLoading) {
            super.rebuildPages();
        }
    }

    @Override
    protected List<IndexViewPage.Entry> getPageEntries() {
        StatsCounter stats = this.minecraft.player.getStats();
        return this.allItemsByMod.entrySet().stream().map(entry -> {
            return IndexViewScreen.IndexViewPage.modItemEntry(entry.getKey(), entry.getValue(), stats, this.font);
        }).sorted(this.getComparator()).toList();
    }

    @Override
    protected void init() {
        this.isLoading = true;
        this.minecraft.getConnection().send(new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.REQUEST_STATS));
        super.init();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float tickDelta) {
        super.render(guiGraphics, mouseX, mouseY, tickDelta);
        if (this.isLoading) {
            guiGraphics.drawString(this.font, DOWNLOAD_PENDING_COMPONENT, (this.width - this.font.width(DOWNLOAD_PENDING_COMPONENT)) / 2, this.topPos + 198 / 2 - 9 * 2, 0x000000, false);
            Component component = Component.literal(LOADING_SYMBOLS[(int) (Util.getMillis() / 150L % (long) LOADING_SYMBOLS.length)]);
            guiGraphics.drawString(this.font, component, (this.width - this.font.width(component)) / 2, this.topPos + 198 / 2, 0x000000, false);
        }
    }
}
