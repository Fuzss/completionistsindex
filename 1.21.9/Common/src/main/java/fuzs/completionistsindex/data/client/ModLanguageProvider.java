package fuzs.completionistsindex.data.client;

import fuzs.completionistsindex.CompletionistsIndex;
import fuzs.completionistsindex.client.CompletionistsIndexClient;
import fuzs.completionistsindex.client.gui.screens.index.IndexGroup;
import fuzs.completionistsindex.client.gui.screens.index.StatsSorting;
import fuzs.puzzleslib.api.client.data.v2.AbstractLanguageProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;

public class ModLanguageProvider extends AbstractLanguageProvider {

    public ModLanguageProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addTranslations(TranslationBuilder builder) {
        builder.addKeyCategory(CompletionistsIndex.MOD_ID, CompletionistsIndex.MOD_NAME);
        builder.add(CompletionistsIndexClient.OPEN_INDEX_KEY_MAPPING, "Open Index");
        builder.add(StatsSorting.CREATIVE.getComponent(), "Creative");
        builder.add(StatsSorting.ALPHABETICALLY.getComponent(), "Alphabetically");
        builder.add(StatsSorting.COLLECTED.getComponent(), "Collected");
        builder.add(IndexGroup.CREATIVE.getComponent(), "Creative");
        builder.add(IndexGroup.MODS.getComponent(), "Mods");
    }
}
