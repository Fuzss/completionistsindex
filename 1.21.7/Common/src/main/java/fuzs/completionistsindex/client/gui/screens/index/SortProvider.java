package fuzs.completionistsindex.client.gui.screens.index;

import fuzs.completionistsindex.client.gui.components.index.IndexViewEntry;
import net.minecraft.network.chat.Component;

import java.util.Comparator;

public interface SortProvider<T> {

    T cycle();

    Component getComponent();

    Comparator<IndexViewEntry> getComparator();
}
