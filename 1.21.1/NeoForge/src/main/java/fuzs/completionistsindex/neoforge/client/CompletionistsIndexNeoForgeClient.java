package fuzs.completionistsindex.neoforge.client;

import fuzs.completionistsindex.CompletionistsIndex;
import fuzs.completionistsindex.client.CompletionistsIndexClient;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = CompletionistsIndex.MOD_ID, dist = Dist.CLIENT)
public class CompletionistsIndexNeoForgeClient {

    public CompletionistsIndexNeoForgeClient() {
        ClientModConstructor.construct(CompletionistsIndex.MOD_ID, CompletionistsIndexClient::new);
    }
}
