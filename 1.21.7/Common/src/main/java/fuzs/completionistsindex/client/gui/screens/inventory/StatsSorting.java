package fuzs.completionistsindex.client.gui.screens.inventory;

import com.google.common.collect.Ordering;
import fuzs.completionistsindex.CompletionistsIndex;
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
    public Comparator<IndexViewScreen.IndexViewPage.Entry> getComparator() {
        return switch (this) {
            case CREATIVE -> Ordering.allEqual()::compare;
            case ALPHABETICALLY -> Comparator.comparing(IndexViewScreen.IndexViewPage.Entry::toComparableKey);
            case COLLECTED -> Comparator.comparing(IndexViewScreen.IndexViewPage.Entry::isCollected)
                    .reversed()
                    .thenComparing(IndexViewScreen.IndexViewPage.Entry::toComparableKey);
        };
    }
}
