package fuzs.completionistsindex.client.handler;

import fuzs.completionistsindex.CompletionistsIndex;
import fuzs.completionistsindex.client.gui.screens.inventory.IndexViewScreen;
import fuzs.completionistsindex.client.gui.screens.inventory.ModsIndexViewScreen;
import fuzs.completionistsindex.config.ClientConfig;
import fuzs.puzzleslib.api.client.gui.v2.components.ScreenElementPositioner;
import fuzs.puzzleslib.api.client.gui.v2.components.SpritelessImageButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public class IndexButtonHandler {
    private static final String[] VANILLA_BUTTON_TRANSLATION_KEYS = {
            "gui.stats",
            "menu.returnToGame",
            "menu.reportBugs",
            "menu.shareToLan"
    };

    @Nullable
    private static AbstractWidget recipeBookButton;
    @Nullable
    private static AbstractWidget collectorsLogButton;

    public static void onAfterInventoryScreenInit(Minecraft minecraft, InventoryScreen screen, int screenWidth, int screenHeight, List<AbstractWidget> widgets, UnaryOperator<AbstractWidget> addWidget, Consumer<AbstractWidget> removeWidget) {
        if (CompletionistsIndex.CONFIG.get(ClientConfig.class).indexButtonScreen ==
                ClientConfig.IndexButtonScreen.PAUSE_MENU) {
            return;
        }
        recipeBookButton = findRecipeBookButton(widgets);
        if (recipeBookButton == null) return;
        collectorsLogButton = new SpritelessImageButton(recipeBookButton.getX() + recipeBookButton.getWidth() + 8,
                recipeBookButton.getY(), 20, 18, 100, 198, 18, IndexViewScreen.INDEX_LOCATION, 512, 256, button -> {
            minecraft.setScreen(new ModsIndexViewScreen(screen, true));
        }
        );
        addWidget.apply(collectorsLogButton);
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

    public static void onAfterPauseScreenInit(Minecraft minecraft, PauseScreen screen, int screenWidth, int screenHeight, List<AbstractWidget> widgets, UnaryOperator<AbstractWidget> addWidget, Consumer<AbstractWidget> removeWidget) {
        if (CompletionistsIndex.CONFIG.get(ClientConfig.class).indexButtonScreen ==
                ClientConfig.IndexButtonScreen.INVENTORY_MENU) {
            return;
        }
        AbstractWidget abstractWidget = new SpritelessImageButton(0, 0, 20, 20, 80, 198, 20,
                IndexViewScreen.INDEX_LOCATION, 512, 256, button -> {
            minecraft.setScreen(new ModsIndexViewScreen(screen, false));
        }
        );
        if (ScreenElementPositioner.tryPositionElement(abstractWidget, widgets, VANILLA_BUTTON_TRANSLATION_KEYS)) {
            addWidget.apply(abstractWidget);
        }
    }
}
