package fuzs.completionistsindex;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import net.fabricmc.api.ModInitializer;

public class CompletionistsIndexFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ModConstructor.construct(CompletionistsIndex.MOD_ID, CompletionistsIndex::new);
    }
}
