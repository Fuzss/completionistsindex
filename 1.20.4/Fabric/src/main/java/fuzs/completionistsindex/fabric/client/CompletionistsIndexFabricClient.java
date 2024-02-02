package fuzs.completionistsindex.fabric.client;

import fuzs.completionistsindex.CompletionistsIndex;
import fuzs.completionistsindex.client.CompletionistsIndexClient;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import net.fabricmc.api.ClientModInitializer;

public class CompletionistsIndexFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientModConstructor.construct(CompletionistsIndex.MOD_ID, CompletionistsIndexClient::new);
    }
}
