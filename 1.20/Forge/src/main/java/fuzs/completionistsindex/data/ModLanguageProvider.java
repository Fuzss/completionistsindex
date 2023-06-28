package fuzs.completionistsindex.data;

import fuzs.puzzleslib.api.data.v1.AbstractLanguageProvider;
import net.minecraft.data.PackOutput;

public class ModLanguageProvider extends AbstractLanguageProvider {

    public ModLanguageProvider(PackOutput packOutput, String modId) {
        super(packOutput, modId);
    }

    @Override
    protected void addTranslations() {
        this.add("completionistsindex.gui.index.sorting.creative", "Creative");
        this.add("completionistsindex.gui.index.sorting.alphabetically", "Alphabetically");
        this.add("completionistsindex.gui.index.sorting.collected", "Collected");
    }
}
