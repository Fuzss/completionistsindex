package fuzs.completionistsindex.client.gui.screens.index;

import com.google.common.collect.Ordering;
import fuzs.completionistsindex.CompletionistsIndex;
import fuzs.completionistsindex.client.gui.components.index.IndexViewEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

import java.util.Comparator;
import java.util.Locale;

public enum StatsSorting implements SortProvider<StatsSorting>, StringRepresentable {
    CREATIVE {
        @Override
        public Comparator<IndexViewEntry<?>> getComparator() {
            return Ordering.allEqual()::compare;
        }
    },
    ALPHABETICALLY {
        @Override
        public Comparator<IndexViewEntry<?>> getComparator() {
            return Comparator.comparing(IndexViewEntry::toComparableKey);
        }
    },
    COLLECTED {
        @Override
        public Comparator<IndexViewEntry<?>> getComparator() {
            return Comparator.<IndexViewEntry<?>, Boolean>comparing(IndexViewEntry::isCollected)
                    .reversed()
                    .thenComparing(IndexViewEntry::toComparableKey);
        }
    };

    private static final StatsSorting[] VALUES = StatsSorting.values();

    private final Component component;

    StatsSorting() {
        this.component = Component.translatable(
                CompletionistsIndex.MOD_ID + ".gui.index.sorting." + this.getSerializedName());
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
    public String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}
