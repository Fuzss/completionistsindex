package fuzs.completionistsindex.client.gui.components.index;

import com.google.common.collect.ImmutableList;
import fuzs.completionistsindex.client.gui.screens.index.ItemsIndexViewScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.StatType;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class IndexViewSingleEntry extends IndexViewEntry<ItemsIndexViewScreen> {
    private boolean isCollected;

    public IndexViewSingleEntry(ItemsIndexViewScreen screen, ItemStack itemStack) {
        super(screen, itemStack, itemStack.getItemName());
    }

    @Override
    public void initialize(StatsCounter statsCounter) {
        super.initialize(statsCounter);
        this.isCollected = this.getStatsValue(statsCounter, this.getItem()) > 0;
    }

    @Override
    protected List<Component> createTooltipLines(ItemStack itemStack, StatsCounter statsCounter) {
        List<Component> tooltipLines = new ArrayList<>();
        tooltipLines.add(itemStack.getStyledHoverName());
        for (StatType<Item> statType : RELEVANT_STAT_TYPES) {
            Component component = statType.getDisplayName();
            int value = statsCounter.getValue(statType, this.getItem());
            if (value > 0) {
                tooltipLines.add(Component.literal(String.valueOf(value))
                        .append(CommonComponents.SPACE)
                        .append(component)
                        .withStyle(ChatFormatting.BLUE));
            }
        }

        return ImmutableList.copyOf(tooltipLines);
    }

    @Override
    public String toComparableKey() {
        return BuiltInRegistries.ITEM.getKey(this.getItem()).getPath();
    }

    @Override
    public boolean isCollected() {
        return this.isCollected;
    }

    @Override
    protected boolean isClickable() {
        return this.screen.getServerPlayer() != null;
    }

    @Override
    protected int getDisplayNameYOffset() {
        return 4;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent) {
        ServerPlayer serverPlayer = this.screen.getServerPlayer();
        if (serverPlayer != null) {
            ServerStatsCounter statsCounter = serverPlayer.getStats();
            Item item = this.getItem();
            if (this.getStatsValue(statsCounter, item) > 0) {
                for (StatType<Item> statType : RELEVANT_STAT_TYPES) {
                    serverPlayer.resetStat(statType.get(item));
                }
            } else {
                serverPlayer.awardStat(RELEVANT_STAT_TYPES.getFirst().get(item));
            }
            this.initialize(statsCounter);
            this.screen.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return true;
        } else {
            return false;
        }
    }
}
