package fuzs.completionistsindex.client.gui.screens.index;

import com.mojang.blaze3d.platform.InputConstants;
import fuzs.completionistsindex.client.gui.components.index.IndexViewEntry;
import fuzs.completionistsindex.client.gui.components.index.IndexViewSingleEntry;
import fuzs.puzzleslib.api.client.gui.v2.components.SpritelessImageButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

public class ItemsIndexViewScreen extends IndexViewScreen<StatsSorting> {
    private static StatsSorting statsSorting = StatsSorting.COLLECTED;
    private final List<ItemStack> items;
    @Nullable
    private final ServerPlayer serverPlayer;
    private boolean isEditingPermitted;

    public ItemsIndexViewScreen(Screen lastScreen, boolean fromInventory, List<ItemStack> items) {
        super(lastScreen, fromInventory);
        this.items = items;
        this.serverPlayer = this.getPlayerFromServer();
    }

    @Nullable
    private ServerPlayer getPlayerFromServer() {
        IntegratedServer integratedServer = Minecraft.getInstance().getSingleplayerServer();

        if (integratedServer != null) {
            ServerPlayer serverPlayer = integratedServer.getPlayerList()
                    .getPlayer(integratedServer.getSingleplayerProfile().getId());

            if (serverPlayer != null && serverPlayer.canUseGameMasterBlocks()) {
                return serverPlayer;
            }
        }

        return null;
    }

    public @Nullable ServerPlayer getServerPlayer() {
        return this.isEditingPermitted ? this.serverPlayer : null;
    }

    @Override
    protected Stream<IndexViewEntry<?>> getPageEntries() {
        StatsCounter statsCounter = this.minecraft.player.getStats();
        return this.items.stream().map((ItemStack itemStack) -> {
            IndexViewSingleEntry indexViewEntry = new IndexViewSingleEntry(this, itemStack);
            indexViewEntry.initialize(statsCounter);
            return indexViewEntry;
        });
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(new SpritelessImageButton(this.leftPos + 17,
                this.topPos + 11,
                16,
                13,
                42,
                202,
                20,
                INDEX_LOCATION,
                512,
                256,
                (Button button) -> {
                    this.minecraft.setScreen(this.lastScreen);
                })).setTooltip(Tooltip.create(CommonComponents.GUI_BACK));
        if (this.serverPlayer != null) {
            this.addRenderableWidget(new SpritelessImageButton(this.leftPos + 316 - 6 - 26 * 2 + 5 - 3,
                    this.topPos - 23 + 5,
                    16,
                    16,
                    this.isEditingPermitted ? 368 + 5 : 342 + 5,
                    45 + 5,
                    16 + 7,
                    INDEX_LOCATION,
                    512,
                    256,
                    (Button button) -> {
                        this.isEditingPermitted = !this.isEditingPermitted;
                        ((SpritelessImageButton) button).xTexStart = this.isEditingPermitted ? 368 + 5 : 342 + 5;
                    }));
        }
        this.rebuildPages();
    }

    @Override
    protected StatsSorting getSortProvider() {
        return statsSorting;
    }

    @Override
    protected void setSortProvider(StatsSorting sortProvider) {
        statsSorting = sortProvider;
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        if (this.serverPlayer != null) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED,
                    INDEX_LOCATION,
                    this.leftPos + 316 - 6 - 26 * 2 - 3,
                    this.topPos - 23,
                    this.isEditingPermitted ? 368 : 342,
                    45,
                    26,
                    23,
                    512,
                    256);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        } else if (keyCode == InputConstants.KEY_BACKSPACE && this.shouldCloseOnEsc()) {
            this.minecraft.setScreen(this.lastScreen);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onClose() {
        if (this.lastScreen != null) {
            this.lastScreen.onClose();
        }
    }
}
