package fuzs.completionistsindex.client;

import fuzs.completionistsindex.client.handler.IndexButtonHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;

public class CompletionistsIndexFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        registerHandlers();
    }

    private static void registerHandlers() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof InventoryScreen) {
                IndexButtonHandler.onScreenInit$Post$1(screen, client, scaledWidth, scaledHeight, Screens.getButtons(screen)::add);
                ScreenMouseEvents.afterMouseClick(screen).register((screen1, mouseX, mouseY, button) -> {
                    IndexButtonHandler.onMouseClicked$Post(screen, mouseX, mouseY, button);
                });
            }
            if (screen instanceof PauseScreen) {
                IndexButtonHandler.onScreenInit$Post$2(screen, client, scaledWidth, scaledHeight, Screens.getButtons(screen)::add);
            }
        });
    }
}
