package fuzs.completionistsindex.client.gui.screens.inventory;

import com.google.common.collect.Ordering;
import net.minecraft.network.chat.Component;

import java.util.Comparator;

public enum StatsSorting {
    CREATIVE("creative"), ALPHABETICALLY("alphabetically"), COLLECTED("collected");

    private final Component component;

    StatsSorting(String translationKey) {
        this.component = Component.translatable("completionistsindex.gui.index.sorting." + translationKey);
    }

    public Component getComponent() {
        return this.component;
    }

    public StatsSorting cycle() {
        StatsSorting[] values = StatsSorting.values();
        return values[(this.ordinal() + 1) % values.length];
    }

    public Comparator<IndexViewScreen.IndexViewPage.Entry> getComparator() {
        return switch (this) {
            case CREATIVE -> Ordering.allEqual()::compare;
            case ALPHABETICALLY -> Comparator.comparing(IndexViewScreen.IndexViewPage.Entry::toComparableKey);
            case COLLECTED -> Comparator.comparing(IndexViewScreen.IndexViewPage.Entry::isCollected).reversed().thenComparing(IndexViewScreen.IndexViewPage.Entry::toComparableKey);
        };
    }
}
