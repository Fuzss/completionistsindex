package fuzs.completionistsindex.neoforge;

import fuzs.completionistsindex.CompletionistsIndex;
import fuzs.completionistsindex.data.client.ModLanguageProvider;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import net.neoforged.fml.common.Mod;

@Mod(CompletionistsIndex.MOD_ID)
public class CompletionistsIndexNeoForge {

    public CompletionistsIndexNeoForge() {
        ModConstructor.construct(CompletionistsIndex.MOD_ID, CompletionistsIndex::new);
        DataProviderHelper.registerDataProviders(CompletionistsIndex.MOD_ID, ModLanguageProvider::new);
    }
}
