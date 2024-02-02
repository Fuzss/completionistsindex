package fuzs.completionistsindex.client.handler;

import fuzs.completionistsindex.CompletionistsIndex;
import fuzs.completionistsindex.client.gui.screens.inventory.IndexViewScreen;
import fuzs.completionistsindex.client.gui.screens.inventory.ModsIndexViewScreen;
import fuzs.completionistsindex.config.ClientConfig;
import fuzs.puzzleslib.api.client.screen.v2.ScreenElementPositioner;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class IndexButtonHandler {
    private static final String[] VANILLA_BUTTON_TRANSLATION_KEYS = {"gui.stats", "menu.returnToGame", "menu.reportBugs", "menu.shareToLan"};

    @Nullable
    private static AbstractWidget recipeBookButton;
    @Nullable
    private static AbstractWidget collectorsLogButton;

    public static void onScreenInit$Post$1(Minecraft minecraft, Screen screen, int screenWidth, int screenHeight, List<AbstractWidget> widgets, Consumer<AbstractWidget> addWidget, Consumer<AbstractWidget> removeWidget) {
        if (!(screen instanceof InventoryScreen)) return;
        if (CompletionistsIndex.CONFIG.get(ClientConfig.class).indexButtonScreen == ClientConfig.IndexButtonScreen.PAUSE_MENU) return;
        recipeBookButton = findRecipeBookButton(widgets);
        if (recipeBookButton == null) return;
        collectorsLogButton = new ImageButton(recipeBookButton.getX() + recipeBookButton.getWidth() + 8, recipeBookButton.getY(), 20, 18, 100, 198, 18, IndexViewScreen.INDEX_LOCATION, 512, 256, button -> {
            minecraft.setScreen(new ModsIndexViewScreen(screen));
        });
        addWidget.accept(collectorsLogButton);
    }

    @Nullable
    private static AbstractWidget findRecipeBookButton(List<AbstractWidget> widgets) {
        for (AbstractWidget widget : widgets) {
            if (widget instanceof ImageButton imageButton) {
                return imageButton;
            }
        }
        return null;
    }

    public static void onMouseClicked$Post(Screen screen, double mouseX, double mouseY, int button) {
        if (collectorsLogButton != null && recipeBookButton != null) {
            collectorsLogButton.setX(recipeBookButton.getX() + recipeBookButton.getWidth() + 8);
            collectorsLogButton.setY(recipeBookButton.getY());
        }
    }

    public static void onScreenInit$Post$2(Minecraft minecraft, Screen screen, int screenWidth, int screenHeight, List<AbstractWidget> widgets, Consumer<AbstractWidget> addWidget, Consumer<AbstractWidget> removeWidget) {
        if (!(screen instanceof PauseScreen)) return;
        if (CompletionistsIndex.CONFIG.get(ClientConfig.class).indexButtonScreen == ClientConfig.IndexButtonScreen.INVENTORY_MENU) return;
        ImageButton imageButton = new ImageButton(0, 0, 20, 20, 80, 198, 20, IndexViewScreen.INDEX_LOCATION, 512, 256, button -> {
            minecraft.setScreen(new ModsIndexViewScreen(screen));
        });
        if (ScreenElementPositioner.tryPositionElement(imageButton, widgets, VANILLA_BUTTON_TRANSLATION_KEYS)) {
            addWidget.accept(imageButton);
        }
    }
}
