package fuzs.completionistsindex;

import fuzs.puzzleslib.core.CommonFactories;
import net.fabricmc.api.ModInitializer;

public class CompletionistsIndexFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        CommonFactories.INSTANCE.modConstructor(CompletionistsIndex.MOD_ID).accept(new CompletionistsIndex());
    }
}
