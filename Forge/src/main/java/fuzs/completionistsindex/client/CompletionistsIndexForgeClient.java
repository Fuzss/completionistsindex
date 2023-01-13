package fuzs.completionistsindex.client;

import fuzs.completionistsindex.CompletionistsIndex;
import fuzs.completionistsindex.client.handler.IndexButtonHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod.EventBusSubscriber(modid = CompletionistsIndex.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CompletionistsIndexForgeClient {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        registerHandlers();
    }

    private static void registerHandlers() {
        MinecraftForge.EVENT_BUS.addListener((final ScreenEvent.Init.Post evt) -> {
            Screen screen = evt.getScreen();
            if (evt.getScreen() instanceof InventoryScreen) {
                IndexButtonHandler.onScreenInit$Post$1(screen, Minecraft.getInstance(), screen.width, screen.height, evt::addListener);
            }
            if (evt.getScreen() instanceof PauseScreen) {
                IndexButtonHandler.onScreenInit$Post$2(screen, Minecraft.getInstance(), screen.width, screen.height, evt::addListener);
            }
        });
        MinecraftForge.EVENT_BUS.addListener((final ScreenEvent.MouseButtonPressed.Post evt) -> {
            if (evt.getScreen() instanceof InventoryScreen && evt.wasHandled()) {
                IndexButtonHandler.onMouseClicked$Post(evt.getScreen(), evt.getMouseX(), evt.getMouseY(), evt.getButton());
            }
        });
    }
}
