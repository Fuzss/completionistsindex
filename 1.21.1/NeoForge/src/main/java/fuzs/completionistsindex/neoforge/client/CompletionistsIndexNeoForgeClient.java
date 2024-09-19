package fuzs.completionistsindex.neoforge.client;

import fuzs.completionistsindex.CompletionistsIndex;
import fuzs.completionistsindex.client.CompletionistsIndexClient;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;

@Mod.EventBusSubscriber(modid = CompletionistsIndex.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CompletionistsIndexNeoForgeClient {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ClientModConstructor.construct(CompletionistsIndex.MOD_ID, CompletionistsIndexClient::new);
    }
}
