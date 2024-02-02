package fuzs.completionistsindex.data;

import fuzs.puzzleslib.api.data.v1.AbstractLanguageProvider;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

public class ModLanguageProvider extends AbstractLanguageProvider {

    public ModLanguageProvider(GatherDataEvent evt, String modId) {
        super(evt, modId);
    }

    @Override
    protected void addTranslations() {
        this.add("completionistsindex.gui.index.sorting.creative", "Creative");
        this.add("completionistsindex.gui.index.sorting.alphabetically", "Alphabetically");
        this.add("completionistsindex.gui.index.sorting.collected", "Collected");
    }
}
