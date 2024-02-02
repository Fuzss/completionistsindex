package fuzs.completionistsindex.neoforge;

import fuzs.completionistsindex.CompletionistsIndex;
import fuzs.completionistsindex.data.client.ModLanguageProvider;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;

@Mod(CompletionistsIndex.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CompletionistsIndexNeoForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ModConstructor.construct(CompletionistsIndex.MOD_ID, CompletionistsIndex::new);
        DataProviderHelper.registerDataProviders(CompletionistsIndex.MOD_ID, ModLanguageProvider::new);
    }
}
