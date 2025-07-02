package fuzs.completionistsindex.client;

import fuzs.completionistsindex.client.handler.IndexButtonHandler;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.event.v1.gui.ScreenEvents;
import fuzs.puzzleslib.api.client.event.v1.gui.ScreenMouseEvents;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;

public class CompletionistsIndexClient implements ClientModConstructor {

    @Override
    public void onConstructMod() {
        registerHandlers();
    }

    private static void registerHandlers() {
        ScreenMouseEvents.afterMouseClick(InventoryScreen.class).register(IndexButtonHandler::onMouseClicked$Post);
        ScreenEvents.afterInit(InventoryScreen.class).register(IndexButtonHandler::onAfterInventoryScreenInit);
        ScreenEvents.afterInit(PauseScreen.class).register(IndexButtonHandler::onAfterPauseScreenInit);
    }
}
