package fuzs.completionistsindex.client;

import fuzs.completionistsindex.CompletionistsIndex;
import fuzs.completionistsindex.client.gui.screens.index.ModsIndexViewScreen;
import fuzs.completionistsindex.client.handler.IndexButtonHandler;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.core.v1.context.KeyMappingsContext;
import fuzs.puzzleslib.api.client.event.v1.gui.ScreenEvents;
import fuzs.puzzleslib.api.client.event.v1.gui.ScreenMouseEvents;
import fuzs.puzzleslib.api.client.key.v1.KeyActivationHandler;
import fuzs.puzzleslib.api.client.key.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;

public class CompletionistsIndexClient implements ClientModConstructor {
    public static final KeyMapping OPEN_INDEX_KEY_MAPPING = KeyMappingHelper.registerUnboundKeyMapping(
            CompletionistsIndex.id("open_index"));

    @Override
    public void onConstructMod() {
        registerEventHandlers();
    }

    private static void registerEventHandlers() {
        ScreenMouseEvents.afterMouseClick(InventoryScreen.class).register(IndexButtonHandler::onAfterMouseClick);
        ScreenEvents.afterInit(InventoryScreen.class).register(IndexButtonHandler::onAfterInventoryScreenInit);
        ScreenEvents.afterInit(PauseScreen.class).register(IndexButtonHandler::onAfterPauseScreenInit);
    }

    @Override
    public void onRegisterKeyMappings(KeyMappingsContext context) {
        context.registerKeyMapping(OPEN_INDEX_KEY_MAPPING, KeyActivationHandler.forGame((Minecraft minecraft) -> {
            minecraft.setScreen(new ModsIndexViewScreen(null, false));
        }));
    }
}
