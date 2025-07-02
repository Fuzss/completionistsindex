package fuzs.completionistsindex.client.gui.screens.inventory;

import net.minecraft.network.chat.Component;

import java.util.Comparator;

public interface SortProvider<T> {

    T cycle();

    Component getComponent();

    Comparator<IndexViewScreen.IndexViewPage.Entry> getComparator();
}
