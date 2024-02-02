package fuzs.completionistsindex.client;

import fuzs.completionistsindex.client.handler.IndexButtonHandler;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.event.v1.ScreenEvents;
import fuzs.puzzleslib.api.client.event.v1.ScreenMouseEvents;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;

public class CompletionistsIndexClient implements ClientModConstructor {

    @Override
    public void onConstructMod() {
        registerHandlers();
    }

    private static void registerHandlers() {
        ScreenMouseEvents.afterMouseClick(InventoryScreen.class).register(IndexButtonHandler::onMouseClicked$Post);
        ScreenEvents.AFTER_INIT.register(IndexButtonHandler::onScreenInit$Post$1);
        ScreenEvents.AFTER_INIT.register(IndexButtonHandler::onScreenInit$Post$2);
    }
}
