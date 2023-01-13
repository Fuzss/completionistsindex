package fuzs.completionistsindex.data;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

public class ModLanguageProvider extends LanguageProvider {

    public ModLanguageProvider(DataGenerator dataGenerator, String modId) {
        super(dataGenerator, modId, "en_us");
    }

    @Override
    protected void addTranslations() {
        this.add("completionistsindex.gui.index.sorting.creative", "Creative");
        this.add("completionistsindex.gui.index.sorting.alphabetically", "Alphabetically");
        this.add("completionistsindex.gui.index.sorting.collected", "Collected");
    }
}
