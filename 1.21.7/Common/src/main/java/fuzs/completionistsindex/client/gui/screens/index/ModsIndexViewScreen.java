package fuzs.completionistsindex.client.gui.screens.index;

import fuzs.completionistsindex.client.gui.components.index.IndexViewEntry;
import fuzs.completionistsindex.client.gui.components.index.IndexViewGroupEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.stats.Stats;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
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
    private static final DecimalFormat PERCENTAGE_FORMAT = new DecimalFormat("#.##");

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
    protected Stream<IndexViewEntry> getPageEntries() {
        StatsCounter statsCounter = this.minecraft.player.getStats();
        return this.items.getOrDefault(indexGroup, Collections.emptyMap())
                .entrySet()
                .stream()
                .map((Map.Entry<Component, List<ItemStack>> entry) -> {
                    return createGroupEntry(entry.getKey(), entry.getValue(), statsCounter, this.font);
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
                this.minecraft.level.registryAccess());
        for (IndexGroup group : IndexGroup.values()) {
            this.items.put(group, group.getGroups());
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

    private static IndexViewEntry createGroupEntry(Component modName, List<ItemStack> items, StatsCounter statsCounter, Font font) {
        if (items.isEmpty()) throw new IllegalArgumentException("items must not be empty");
        ItemStack displayItem = items.get(RANDOM.nextInt(items.size()));
        long collectedCount = items.stream().filter((ItemStack stack) -> {
            int pickedUp = statsCounter.getValue(Stats.ITEM_PICKED_UP, stack.getItem());
            int crafted = statsCounter.getValue(Stats.ITEM_CRAFTED, stack.getItem());
            return pickedUp + crafted > 0;
        }).count();
        boolean collected = collectedCount == items.size();
        float collectionProgress = collectedCount / (float) items.size();
        Component tooltipComponent = Component.empty()
                .append(modName)
                .append(Component.literal(" (" + PERCENTAGE_FORMAT.format(collectionProgress * 100.0F) + "%)")
                        .withStyle(ChatFormatting.GOLD));
        Component formattedName = IndexViewPage.formatDisplayName(font, modName, collected, false);
        return new IndexViewGroupEntry(displayItem,
                formattedName,
                collected,
                Collections.singletonList(tooltipComponent),
                Component.literal(collectedCount + "/" + items.size()),
                collectionProgress,
                items);
    }
}
