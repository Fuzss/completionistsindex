package fuzs.completionistsindex.client.gui.screens.index;

import fuzs.completionistsindex.client.gui.components.index.IndexViewEntry;
import fuzs.completionistsindex.client.gui.components.index.IndexViewGroupEntry;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class ModsIndexViewScreen extends IndexViewScreen<IndexGroup> {
    private static final Component PENDING_TEXT = Component.translatable("multiplayer.downloadingStats");
    private static final String[] LOADING_SYMBOLS = new String[]{
            "oooooo", "Oooooo", "oOoooo", "ooOooo", "oooOoo", "ooooOo", "oooooO"
    };

    private static IndexGroup indexGroup = IndexGroup.CREATIVE;
    private final Map<IndexGroup, Map<Component, List<ItemStack>>> items = new EnumMap<>(IndexGroup.class);
    private boolean isLoading = true;

    public ModsIndexViewScreen(@Nullable Screen lastScreen, boolean fromInventory) {
        super(lastScreen, fromInventory);
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
    protected Stream<IndexViewEntry<?>> getPageEntries() {
        StatsCounter statsCounter = this.minecraft.player.getStats();
        return this.items.getOrDefault(indexGroup, Collections.emptyMap())
                .entrySet()
                .stream()
                .map((Map.Entry<Component, List<ItemStack>> entry) -> {
                    IndexViewGroupEntry indexViewEntry = new IndexViewGroupEntry(this,
                            entry.getValue(),
                            entry.getKey());
                    indexViewEntry.initialize(statsCounter);
                    return indexViewEntry;
                });
    }

    @Override
    protected void init() {
        this.items.clear();
        this.isLoading = true;
        this.minecraft.getConnection()
                .send(new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.REQUEST_STATS));
        super.init();
        // always set hasPermissions to false, we do not want unobtainable items in the index
        CreativeModeTabs.tryRebuildTabContents(this.minecraft.player.connection.enabledFeatures(),
                false,
                this.minecraft.getConnection().registryAccess());
        for (IndexGroup group : IndexGroup.values()) {
            this.items.put(group, group.getDisplayGroups());
        }
    }

    @Override
    protected IndexGroup getSortProvider() {
        return indexGroup;
    }

    @Override
    protected void setSortProvider(IndexGroup sortProvider) {
        indexGroup = sortProvider;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        if (this.isLoading) {
            guiGraphics.drawString(this.font,
                    PENDING_TEXT,
                    (this.width - this.font.width(PENDING_TEXT)) / 2,
                    this.topPos + 198 / 2 - 9 * 2,
                    0,
                    false);
            Component component = Component.literal(LOADING_SYMBOLS[(int) (Util.getMillis() / 150L
                    % (long) LOADING_SYMBOLS.length)]);
            guiGraphics.drawString(this.font,
                    component,
                    (this.width - this.font.width(component)) / 2,
                    this.topPos + 198 / 2,
                    0,
                    false);
        }
    }
}
