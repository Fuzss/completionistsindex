package fuzs.completionistsindex.client.gui.screens.inventory;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.completionistsindex.CompletionistsIndex;
import fuzs.completionistsindex.config.ClientConfig;
import net.minecraft.Util;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.achievement.StatsUpdateListener;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ModsIndexViewScreen extends IndexViewScreen implements StatsUpdateListener {
    private static final Component PENDING_TEXT = Component.translatable("multiplayer.downloadingStats");

    private final Map<String, List<ItemStack>> allItemsByMod = getAllItemsByMod();
    private boolean isLoading = true;

    public ModsIndexViewScreen(Screen lastScreen) {
        super(lastScreen);
    }

    private static Map<String, List<ItemStack>> getAllItemsByMod() {
        NonNullList<ItemStack> searchTabItems = NonNullList.create();
        CreativeModeTab.TAB_SEARCH.fillItemList(searchTabItems);
        // use linked hash map to maybe preserve mod order
        Map<String, List<ItemStack>> allItems = searchTabItems.stream()
                .map(ItemStack::getItem)
                .distinct()
                .filter(Predicate.not(CompletionistsIndex.CONFIG.get(ClientConfig.class).blacklist::contains))
                .collect(Collectors.groupingBy(item -> Registry.ITEM.getKey(item).getNamespace(), LinkedHashMap::new, Collectors.mapping(ItemStack::new, Collectors.toList())));
        return ImmutableMap.copyOf(allItems);
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
    public void render(PoseStack poseStack, int mouseX, int mouseY, float tickDelta) {
        super.render(poseStack, mouseX, mouseY, tickDelta);
        if (this.isLoading) {
            Component component = PENDING_TEXT;
            this.font.draw(poseStack, component, (this.width - this.font.width(component)) / 2, this.topPos + 198 / 2 - 9 * 2, 0x000000);
            component = Component.literal(LOADING_SYMBOLS[(int) (Util.getMillis() / 150L % (long) LOADING_SYMBOLS.length)]);
            this.font.draw(poseStack, component, (this.width - this.font.width(component)) / 2, this.topPos + 198 / 2, 0x000000);
        }
    }
}
