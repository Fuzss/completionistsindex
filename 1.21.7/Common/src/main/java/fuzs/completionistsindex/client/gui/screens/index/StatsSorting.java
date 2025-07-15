package fuzs.completionistsindex.client.gui.screens.index;

import com.google.common.collect.Ordering;
import fuzs.completionistsindex.CompletionistsIndex;
import fuzs.completionistsindex.client.gui.components.index.IndexViewEntry;
import net.minecraft.network.chat.Component;

import java.util.Comparator;

public enum StatsSorting implements SortProvider<StatsSorting> {
    CREATIVE("creative"),
    ALPHABETICALLY("alphabetically"),
    COLLECTED("collected");

    private static final StatsSorting[] VALUES = StatsSorting.values();

    private final Component component;

    StatsSorting(String translationKey) {
        this.component = Component.translatable(CompletionistsIndex.MOD_ID + ".gui.index.sorting." + translationKey);
    }

    @Override
    public Component getComponent() {
        return this.component;
    }

    @Override
    public StatsSorting cycle() {
        return VALUES[(this.ordinal() + 1) % VALUES.length];
    }

    @Override
    public Comparator<IndexViewEntry> getComparator() {
        return switch (this) {
            case CREATIVE -> Ordering.allEqual()::compare;
            case ALPHABETICALLY -> Comparator.comparing(IndexViewEntry::toComparableKey);
            case COLLECTED -> Comparator.comparing(IndexViewEntry::isCollected)
                    .reversed()
                    .thenComparing(IndexViewEntry::toComparableKey);
        };
    }
}
