package fuzs.completionistsindex.client.gui.screens.index;

import com.google.common.collect.ImmutableList;
import fuzs.completionistsindex.CompletionistsIndex;
import fuzs.completionistsindex.client.gui.components.index.IndexViewEntry;
import fuzs.puzzleslib.api.client.gui.v2.components.SpritelessImageButton;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.navigation.ScreenAxis;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public abstract class IndexViewScreen<T extends SortProvider<T>> extends StatsUpdateListener {
    public static final ResourceLocation INDEX_LOCATION = CompletionistsIndex.id("textures/gui/index.png");
    private static final Component PREVIOUS_PAGE_COMPONENT = Component.translatable("spectatorMenu.previous_page");
    private static final Component NEXT_PAGE_COMPONENT = Component.translatable("spectatorMenu.next_page");
    private static final Component SEARCH_HINT = Component.translatable("gui.recipebook.search_hint")
            .withStyle(ChatFormatting.ITALIC)
            .withStyle(ChatFormatting.GRAY);
    public static final RandomSource RANDOM = RandomSource.create();

    private final boolean fromInventory;
    protected int leftPos;
    protected int topPos;
    private Button turnPageBackwards;
    private Button turnPageForwards;
    private int currentPage;
    private Component leftPageIndicator;
    private Component rightPageIndicator;
    private List<IndexViewPage> pages;
    @Nullable
    private EditBox searchBox;
    private String lastSearch = "";
    private boolean ignoreTextInput;
    @Nullable
    private ScreenRectangle magnifierIconPlacement;
    private long randomSeed;

    protected IndexViewScreen(@Nullable Screen lastScreen, boolean fromInventory) {
        super(lastScreen);
        this.fromInventory = fromInventory;
    }

    protected abstract Stream<IndexViewEntry<?>> getPageEntries();

    protected void rebuildPages() {
        RANDOM.setSeed(this.randomSeed);
        List<IndexViewEntry<?>> entries = this.getPageEntries().filter((IndexViewEntry<?> entry) -> {
            return entry.getDisplayNameString().toLowerCase(Locale.ROOT).contains(this.getSearchQuery());
        }).sorted(this.getSortProvider().getComparator()).toList();
        this.pages = IndexViewPage.createPages(this, entries);
        this.setCurrentPage(0);
    }

    @Override
    protected void init() {
        this.leftPos = (this.width - 316) / 2;
        this.topPos = (this.height - 198) / 2;
        this.randomSeed = RANDOM.nextLong();
        this.magnifierIconPlacement = ScreenRectangle.of(ScreenAxis.HORIZONTAL,
                this.leftPos + (316 / 2 - 146) / 2 + 18,
                this.topPos - 23 + 5,
                16,
                16);
        this.searchBox = new EditBox(this.font,
                this.leftPos + (316 / 2 - 146) / 2 + 43,
                this.topPos - 23 + 6,
                81,
                this.font.lineHeight + 5,
                Component.translatable("itemGroup.search"));
        this.searchBox.setMaxLength(50);
        this.searchBox.setVisible(true);
        this.searchBox.setTextColor(-1);
        this.searchBox.setHint(SEARCH_HINT);
        this.addRenderableWidget(new SpritelessImageButton(this.leftPos + 316 - 6 - 26 + 5,
                this.topPos - 23 + 5,
                16,
                16,
                316 + 5,
                45 + 5,
                16 + 7,
                INDEX_LOCATION,
                512,
                256,
                (Button button) -> {
                    this.onClose();
                }));
        this.addRenderableWidget(new SpritelessImageButton(this.leftPos + 316 - 17 - 16,
                this.topPos + 11,
                16,
                13,
                62,
                202,
                20,
                INDEX_LOCATION,
                512,
                256,
                (Button button) -> {
                    this.setSortProvider(this.getSortProvider().cycle());
                    button.setTooltip(Tooltip.create(this.getSortProvider().getComponent()));
                    this.rebuildPages();
                })).setTooltip(Tooltip.create(this.getSortProvider().getComponent()));
        this.turnPageBackwards = this.addRenderableWidget(new SpritelessImageButton(this.leftPos + 27,
                this.topPos + 173,
                18,
                10,
                1,
                203,
                20,
                INDEX_LOCATION,
                512,
                256,
                (Button button) -> {
                    this.decrementPage();
                }));
        this.turnPageBackwards.setTooltip(Tooltip.create(PREVIOUS_PAGE_COMPONENT));
        this.turnPageForwards = this.addRenderableWidget(new SpritelessImageButton(this.leftPos + 316 - 27 - 18,
                this.topPos + 173,
                18,
                10,
                21,
                203,
                20,
                INDEX_LOCATION,
                512,
                256,
                (Button button) -> {
                    this.incrementPage();
                }));
        this.turnPageForwards.setTooltip(Tooltip.create(NEXT_PAGE_COMPONENT));
        this.setCurrentPage(this.currentPage);
        this.resetLastSearch();
    }

    public boolean isFromInventory() {
        return this.fromInventory;
    }

    private void resetLastSearch() {
        this.lastSearch = "";
    }

    protected abstract T getSortProvider();

    protected abstract void setSortProvider(T sortProvider);

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.fromInventory) {
            this.renderTransparentBackground(guiGraphics);
        } else {
            super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        }
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED,
                INDEX_LOCATION,
                this.leftPos + (316 / 2 - 146) / 2,
                this.topPos - 23,
                316,
                22,
                146,
                23,
                512,
                256);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED,
                INDEX_LOCATION,
                this.leftPos + 316 - 6 - 26,
                this.topPos - 23,
                316,
                45,
                26,
                23,
                512,
                256);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED,
                INDEX_LOCATION,
                this.leftPos,
                this.topPos,
                0,
                0,
                316,
                198,
                512,
                256);
        guiGraphics.drawString(this.font,
                this.leftPageIndicator,
                this.leftPos + 82 - this.font.width(this.leftPageIndicator) / 2,
                this.topPos + 13,
                0xFFB8A48A,
                false);
        guiGraphics.drawString(this.font,
                this.rightPageIndicator,
                this.leftPos + 233 - this.font.width(this.rightPageIndicator) / 2,
                this.topPos + 13,
                0xFFB8A48A,
                false);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.setFocused(null);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.searchBox.render(guiGraphics, mouseX, mouseY, partialTick);
        if (this.pages != null && !this.pages.isEmpty()) {
            this.pages.get(this.currentPage).render(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.searchBox.setFocused(false);
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        } else if (this.pages != null && !this.pages.isEmpty() && this.pages.get(this.currentPage)
                .mouseClicked((int) mouseX, (int) mouseY, button)) {
            return true;
        } else {
            boolean mouseClickedOnMagnifier =
                    this.magnifierIconPlacement != null && this.magnifierIconPlacement.containsPoint(Mth.floor(mouseX),
                            Mth.floor(mouseY));
            if (mouseClickedOnMagnifier || this.searchBox.mouseClicked(mouseX, mouseY, button)) {
                this.searchBox.setFocused(true);
                return true;
            } else {
                return false;
            }
        }
    }

    private void decrementPage() {
        if (this.currentPage > 0) this.setCurrentPage(this.currentPage - 1);
    }

    private void incrementPage() {
        if (this.currentPage < this.getAllPages() - 1) this.setCurrentPage(this.currentPage + 1);
    }

    private void setCurrentPage(int newPage) {
        this.currentPage = newPage;
        this.turnPageBackwards.visible = this.turnPageForwards.visible = true;
        if (newPage == 0) this.turnPageBackwards.visible = false;
        if (newPage >= this.getAllPages() - 1) this.turnPageForwards.visible = false;
        this.leftPageIndicator = Component.translatable("book.pageIndicator", newPage * 2 + 1, this.getAllPages() * 2);
        this.rightPageIndicator = Component.translatable("book.pageIndicator", newPage * 2 + 2, this.getAllPages() * 2);
    }

    private int getAllPages() {
        return this.pages != null && !this.pages.isEmpty() ? this.pages.size() : 1;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (scrollX > 0.0 || scrollY > 0.0) {
            this.decrementPage();
            return true;
        } else if (scrollX < 0.0 || scrollY < 0.0) {
            this.incrementPage();
            return true;
        } else {
            return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        this.ignoreTextInput = false;
        if (this.searchBox.keyPressed(keyCode, scanCode, modifiers)) {
            this.checkSearchStringUpdate();
            return true;
        } else if (this.minecraft.options.keyChat.matches(keyCode, scanCode) && !this.searchBox.isFocused()) {
            this.ignoreTextInput = true;
            this.searchBox.setFocused(true);
            return true;
        } else {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        this.ignoreTextInput = false;
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (this.ignoreTextInput) {
            return false;
        } else if (this.searchBox.charTyped(codePoint, modifiers)) {
            this.checkSearchStringUpdate();
            return true;
        } else {
            return super.charTyped(codePoint, modifiers);
        }
    }

    private String getSearchQuery() {
        return this.searchBox != null ? this.searchBox.getValue().trim().toLowerCase(Locale.ROOT) : "";
    }

    private void checkSearchStringUpdate() {
        String string = this.getSearchQuery();
        if (!string.equals(this.lastSearch)) {
            this.rebuildPages();
            this.lastSearch = string;
        }
    }

    protected static class IndexViewPage implements Renderable {
        private final IndexViewEntry<?>[] entries = new IndexViewEntry[14];
        private final IndexViewScreen<?> screen;

        private IndexViewPage(IndexViewScreen<?> screen) {
            this.screen = screen;
        }

        public static List<IndexViewPage> createPages(IndexViewScreen<?> screen, List<IndexViewEntry<?>> entries) {
            ImmutableList.Builder<IndexViewPage> builder = ImmutableList.builder();
            IndexViewPage page = null;
            int itemsCount = 0;
            for (IndexViewEntry<?> entry : entries) {
                if (page == null) {
                    page = new IndexViewPage(screen);
                    builder.add(page);
                }
                page.entries[itemsCount] = entry;
                if (++itemsCount >= 14) {
                    itemsCount = 0;
                    page = null;
                }
            }
            return builder.build();
        }

        @Override
        public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            this.renderPageSide(guiGraphics,
                    mouseX,
                    mouseY,
                    partialTick,
                    this.screen.leftPos + 16,
                    this.screen.topPos + 26,
                    0,
                    7);
            this.renderPageSide(guiGraphics,
                    mouseX,
                    mouseY,
                    partialTick,
                    this.screen.leftPos + 167,
                    this.screen.topPos + 26,
                    7,
                    14);
        }

        private void renderPageSide(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, int startX, int startY, int startIndex, int endIndex) {
            for (int i = startIndex, posY = startY; i < endIndex; i++) {
                IndexViewEntry<?> indexViewEntry = this.entries[i];
                if (indexViewEntry != null) {
                    indexViewEntry.renderWithTooltip(this.screen.font,
                            guiGraphics,
                            mouseX,
                            mouseY,
                            partialTick,
                            startX,
                            posY);
                    posY += 21;
                } else {
                    break;
                }
            }
        }

        public boolean mouseClicked(int mouseX, int mouseY, int buttonId) {
            for (int i = 0; i < this.entries.length; i++) {
                IndexViewEntry<?> indexViewEntry = this.entries[i];
                if (indexViewEntry != null) {
                    int posX = i >= 7 ? this.screen.leftPos + 167 : this.screen.leftPos + 16;
                    int posY = this.screen.topPos + 26 + i % 7 * 21;
                    if (indexViewEntry.isMouseOver(posX, posY, mouseX, mouseY)) {
                        return indexViewEntry.mouseClicked(mouseX, mouseY, buttonId);
                    }
                } else {
                    return false;
                }
            }

            return false;
        }
    }
}