package fuzs.completionistsindex.client.gui.screens.inventory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import fuzs.completionistsindex.CompletionistsIndex;
import fuzs.puzzleslib.api.client.gui.v2.components.SpritelessImageButton;
import fuzs.puzzleslib.api.util.v1.ComponentHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.stats.StatsCounter;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public abstract class IndexViewScreen extends StatsUpdateListener {
    public static final ResourceLocation INDEX_LOCATION = CompletionistsIndex.id("textures/gui/index.png");
    private static final MutableComponent PREVIOUS_PAGE_COMPONENT = Component.translatable("spectatorMenu.previous_page");
    private static final MutableComponent NEXT_PAGE_COMPONENT = Component.translatable("spectatorMenu.next_page");
    private static final ResourceLocation SLOT_HIGHLIGHT_BACK_SPRITE = ResourceLocation.withDefaultNamespace(
            "container/slot_highlight_back");
    private static final ResourceLocation SLOT_HIGHLIGHT_FRONT_SPRITE = ResourceLocation.withDefaultNamespace(
            "container/slot_highlight_front");

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
    private List<Component> tooltipLines;

    protected IndexViewScreen(Screen lastScreen, boolean fromInventory) {
        super(lastScreen);
        this.fromInventory = fromInventory;
    }

    protected abstract List<IndexViewPage.Entry> getPageEntries();

    protected void rebuildPages() {
        this.pages = IndexViewPage.createPages(this, this.getPageEntries());
        this.setCurrentPage(0);
    }

    @Override
    protected void init() {
        this.leftPos = (this.width - 316) / 2;
        this.topPos = (this.height - 198) / 2;
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
                button -> {
                    this.onClose();
                })).setTooltip(Tooltip.create(CommonComponents.GUI_BACK));
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
                button -> {
                    this.cyclePageContents();
                    button.setTooltip(Tooltip.create(this.getTooltipComponent()));
                    this.rebuildPages();
                })).setTooltip(Tooltip.create(this.getTooltipComponent()));
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
                button -> {
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
                button -> {
                    this.incrementPage();
                }));
        this.turnPageForwards.setTooltip(Tooltip.create(NEXT_PAGE_COMPONENT));
        this.setCurrentPage(this.currentPage);
    }

    protected abstract void cyclePageContents();

    protected abstract Component getTooltipComponent();

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.fromInventory) {
            this.renderTransparentBackground(guiGraphics);
        } else {
            super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        }
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.blit(RenderType::guiTextured, INDEX_LOCATION, this.leftPos, this.topPos, 0, 0, 316, 198, 512, 256);
        guiGraphics.drawString(this.font,
                this.leftPageIndicator,
                this.leftPos + 82 - this.font.width(this.leftPageIndicator) / 2,
                this.topPos + 13,
                0xB8A48A,
                false);
        guiGraphics.drawString(this.font,
                this.rightPageIndicator,
                this.leftPos + 233 - this.font.width(this.rightPageIndicator) / 2,
                this.topPos + 13,
                0xB8A48A,
                false);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float tickDelta) {
        this.tooltipLines = null;
        this.setFocused(null);
        super.render(guiGraphics, mouseX, mouseY, tickDelta);
        if (this.pages != null && !this.pages.isEmpty()) {
            this.pages.get(this.currentPage).render(guiGraphics, mouseX, mouseY, tickDelta);
        }
        if (this.tooltipLines != null) {
            guiGraphics.renderTooltip(this.font, this.tooltipLines, Optional.empty(), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int buttonId) {
        if (!super.mouseClicked(mouseX, mouseY, buttonId) && this.pages != null && !this.pages.isEmpty()) {
            return this.pages.get(this.currentPage).mouseClicked((int) mouseX, (int) mouseY, buttonId);
        }
        return false;
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
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        } else if (keyCode == InputConstants.KEY_BACKSPACE && this.shouldCloseOnEsc()) {
            this.onClose();
            return true;
        } else {
            return false;
        }
    }

    public static class IndexViewPage implements Renderable {
        private static final DecimalFormat PERCENTAGE_FORMAT = new DecimalFormat("#.##");

        private final Entry[] entries = new Entry[14];
        private final IndexViewScreen screen;

        private IndexViewPage(IndexViewScreen screen) {
            this.screen = screen;
        }

        public static List<IndexViewPage> createPages(IndexViewScreen screen, List<Entry> entries) {
            ImmutableList.Builder<IndexViewPage> builder = ImmutableList.builder();
            IndexViewPage page = null;
            int itemsCount = 0;
            for (Entry entry : entries) {
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
            int posX = startX;
            int posY = startY;
            for (int i = startIndex; i < endIndex; i++) {
                Entry entry = this.entries[i];
                if (entry != null) {
                    int mouseXOffset = mouseX - startX;
                    int mouseYOffset = mouseY - startY - i % 7 * 21;
                    boolean isHoveringSlot = entry.isHoveringSlot(mouseXOffset, mouseYOffset);
                    if (isHoveringSlot) {
                        guiGraphics.blitSprite(RenderType::guiTextured,
                                SLOT_HIGHLIGHT_BACK_SPRITE,
                                posX + 1 - 4,
                                posY + 1 - 4,
                                24,
                                24);
                    }
                    entry.render(this.screen.minecraft,
                            guiGraphics,
                            mouseXOffset,
                            mouseYOffset,
                            partialTick,
                            posX,
                            posY);
                    if (isHoveringSlot) {
                        guiGraphics.blitSprite(RenderType::guiTexturedOverlay,
                                SLOT_HIGHLIGHT_FRONT_SPRITE,
                                posX + 1 - 4,
                                posY + 1 - 4,
                                24,
                                24);
                        this.screen.tooltipLines = entry.getTooltipLines();
                    }
                    posY += 21;
                } else {
                    break;
                }
            }
        }

        public boolean mouseClicked(int mouseX, int mouseY, int buttonId) {
            for (int i = 0; i < this.entries.length; i++) {
                Entry entry = this.entries[i];
                if (entry == null) return false;
                int startX = i >= 7 ? this.screen.leftPos + 167 : this.screen.leftPos + 16;
                int startY = this.screen.topPos + 26;
                if (entry.isMouseOver(mouseX - startX, mouseY - startY - i % 7 * 21)) {
                    return entry.mouseClicked(this.screen, mouseX, mouseY, buttonId);
                }
            }
            return false;
        }

        public static Entry createGroupEntry(Component modName, List<ItemStack> items, StatsCounter statsCounter, Font font) {
            if (items.isEmpty()) throw new IllegalArgumentException("items must not be empty");
            ItemStack displayItem = items.stream().skip((int) (Math.random() * items.size())).findAny().orElseThrow();
            long collectedCount = items.stream().filter((ItemStack stack) -> {
                int pickedUp = statsCounter.getValue(Stats.ITEM_PICKED_UP, stack.getItem());
                int crafted = statsCounter.getValue(Stats.ITEM_CRAFTED, stack.getItem());
                return pickedUp + crafted > 0;
            }).count();
            boolean collected = collectedCount == items.size();
            float collectionProgress = collectedCount / (float) items.size();
            Component tooltipComponent = Component.empty()
                    .append(modName)
                    .append(Component.literal(" (" + PERCENTAGE_FORMAT.format(collectionProgress * 100.0F) + "%)")
                            .withStyle(ChatFormatting.GOLD));
            Component formattedName = formatDisplayName(font, modName, collected, false);
            return new GroupEntry(displayItem,
                    formattedName,
                    collected,
                    Collections.singletonList(tooltipComponent),
                    Component.literal(collectedCount + "/" + items.size()),
                    collectionProgress,
                    items);
        }

        public static Entry createSingleEntry(ItemStack itemStack, StatsCounter statsCounter, Font font) {
            int pickedUp = statsCounter.getValue(Stats.ITEM_PICKED_UP, itemStack.getItem());
            int crafted = statsCounter.getValue(Stats.ITEM_CRAFTED, itemStack.getItem());
            boolean collected = pickedUp > 0 || crafted > 0;
            Component displayName = itemStack.getItem().getName(itemStack);
            Component formattedName = formatDisplayName(font, displayName, collected, true);
            List<Component> lines = Lists.newArrayList();
            lines.add(Component.empty()
                    .append(itemStack.getItem().getName(itemStack))
                    .withStyle(itemStack.getRarity().color()));
            itemStack.getItem().appendHoverText(itemStack, Item.TooltipContext.EMPTY, lines, TooltipFlag.NORMAL);
            if (pickedUp > 0) {
                lines.add(Component.literal(String.valueOf(pickedUp))
                        .append(" ")
                        .append(Component.translatable("stat_type.minecraft.picked_up"))
                        .withStyle(ChatFormatting.BLUE));
            }
            if (crafted > 0) {
                lines.add(Component.literal(String.valueOf(crafted))
                        .append(" ")
                        .append(Component.translatable("stat_type.minecraft.crafted"))
                        .withStyle(ChatFormatting.BLUE));
            }
            return new SingleEntry(itemStack, formattedName, collected, ImmutableList.copyOf(lines));
        }

        private static Component formatDisplayName(Font font, Component displayName, boolean collected, boolean fullLength) {
            Style style = Style.EMPTY.withColor(collected ? 0x4BA52F : ChatFormatting.BLACK.getColor());
            MutableComponent component;
            if (!fullLength && font.width(displayName) > 95) {
                FormattedText formattedText = font.getSplitter()
                        .headByWidth(displayName, 95 - font.width(CommonComponents.ELLIPSIS), style);
                component = Component.empty()
                        .append(ComponentHelper.toComponent(formattedText))
                        .append(CommonComponents.ELLIPSIS);
            } else {
                component = Component.empty().append(displayName);
            }
            return component.withStyle(style);
        }

        public static abstract class Entry {
            final ItemStack item;
            final Component displayName;
            private final boolean collected;
            private final List<Component> tooltipLines;

            private Entry(ItemStack item, Component displayName, boolean collected, List<Component> tooltipLines) {
                this.item = item;
                this.displayName = displayName;
                this.collected = collected;
                this.tooltipLines = tooltipLines;
            }

            public abstract <T extends Comparable<? super T>> T toComparableKey();

            public boolean isCollected() {
                return this.collected;
            }

            List<Component> getTooltipLines() {
                return this.tooltipLines;
            }

            public void render(Minecraft minecraft, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, int posX, int posY) {
                this.renderBackground(guiGraphics, mouseX, mouseY, partialTick, posX, posY);
                this.renderForeground(minecraft, guiGraphics, mouseX, mouseY, partialTick, posX, posY);
            }

            public boolean isHoveringSlot(int mouseX, int mouseY) {
                return this.isHovering(0, 0, 16, 16, mouseX, mouseY);
            }

            public boolean isMouseOver(int mouseX, int mouseY) {
                return this.isHovering(0, 0, 134, 18, mouseX, mouseY);
            }

            public boolean mouseClicked(Screen screen, int mouseX, int mouseY, int buttonId) {
                return false;
            }

            private boolean isHovering(int minX, int minY, int maxX, int maxY, int mouseX, int mouseY) {
                return mouseX > minX && mouseX <= maxX && mouseY > minY && mouseY <= maxY;
            }

            public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, int posX, int posY) {
                guiGraphics.blit(RenderType::guiTextured, INDEX_LOCATION, posX, posY, 120, 208, 18, 18, 512, 256);
                guiGraphics.blit(RenderType::guiTextured,
                        INDEX_LOCATION,
                        posX + 124,
                        posY + 4,
                        120 + (this.collected ? 10 : 0),
                        198,
                        10,
                        10,
                        512,
                        256);
            }

            public void renderForeground(Minecraft minecraft, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, int posX, int posY) {
                guiGraphics.renderItem(this.item, posX + 1, posY + 1);
            }
        }

        private static class SingleEntry extends Entry {

            public SingleEntry(ItemStack item, Component displayName, boolean collected, List<Component> tooltipLines) {
                super(item, displayName, collected, tooltipLines);
            }

            @SuppressWarnings("unchecked")
            @Override
            public <T extends Comparable<? super T>> T toComparableKey() {
                return (T) BuiltInRegistries.ITEM.getKey(this.item.getItem()).getPath();
            }

            @Override
            public void renderForeground(Minecraft minecraft, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, int posX, int posY) {
                super.renderForeground(minecraft, guiGraphics, mouseX, mouseY, partialTick, posX, posY);
                renderScrollingString(guiGraphics,
                        minecraft.font,
                        this.displayName,
                        posX + 23,
                        posY + 4,
                        posX + 23 + 95,
                        posY + 4 + minecraft.font.lineHeight,
                        0);
            }

            /**
             * Copied from
             * {@link net.minecraft.client.gui.components.AbstractWidget#renderScrollingString(GuiGraphics, Font,
             * Component, int, int, int, int, int)}.
             * <p>
             * Allows for rendering without enabled {@code dropShadow}.
             */
            protected static void renderScrollingString(GuiGraphics guiGraphics, Font font, Component text, int minX, int minY, int maxX, int maxY, int color) {
                renderScrollingString(guiGraphics, font, text, (minX + maxX) / 2, minX, minY, maxX, maxY, color);
            }

            /**
             * Copied from
             * {@link net.minecraft.client.gui.components.AbstractWidget#renderScrollingString(GuiGraphics, Font,
             * Component, int, int, int, int, int, int)}.
             * <p>
             * Allows for rendering without enabled {@code dropShadow}.
             */
            protected static void renderScrollingString(GuiGraphics guiGraphics, Font font, Component text, int centerX, int minX, int minY, int maxX, int maxY, int color) {
                int i = font.width(text);
                int j = (minY + maxY - 9) / 2 + 1;
                int k = maxX - minX;
                if (i > k) {
                    int l = i - k;
                    double d = (double) Util.getMillis() / 1000.0;
                    double e = Math.max((double) l * 0.5, 3.0);
                    double f = Math.sin((Math.PI / 2) * Math.cos((Math.PI * 2) * d / e)) / 2.0 + 0.5;
                    double g = Mth.lerp(f, 0.0, l);
                    guiGraphics.enableScissor(minX, minY, maxX, maxY);
                    guiGraphics.drawString(font, text, minX - (int) g, j, color, false);
                    guiGraphics.disableScissor();
                } else {
                    int l = Mth.clamp(centerX, minX + i / 2, maxX - i / 2);
                    FormattedCharSequence formattedCharSequence = text.getVisualOrderText();
                    guiGraphics.drawString(font, text, l - font.width(formattedCharSequence) / 2, j, color, false);
                }
            }
        }

        public static class GroupEntry extends Entry {
            private final Component collection;
            private final float collectionProgress;
            private final List<ItemStack> items;

            public GroupEntry(ItemStack item, Component displayName, boolean collected, List<Component> tooltipLines, Component collection, float collectionProgress, List<ItemStack> items) {
                super(item, displayName, collected, tooltipLines);
                this.collection = collection;
                this.collectionProgress = collectionProgress;
                this.items = items;
            }

            @SuppressWarnings("unchecked")
            @Override
            public <T extends Comparable<? super T>> T toComparableKey() {
                return (T) this.displayName.getString();
            }

            @Override
            public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, int posX, int posY) {
                super.renderBackground(guiGraphics, mouseX, mouseY, partialTick, posX, posY);
                guiGraphics.blit(RenderType::guiTextured,
                        INDEX_LOCATION,
                        posX + 24,
                        posY + 11,
                        140,
                        198,
                        91,
                        5,
                        512,
                        256);
                guiGraphics.blit(RenderType::guiTextured,
                        INDEX_LOCATION,
                        posX + 24,
                        posY + 11,
                        140,
                        203,
                        (int) (91 * this.collectionProgress),
                        5,
                        512,
                        256);
                if (this.isMouseOver(mouseX, mouseY)) {
                    guiGraphics.blit(RenderType::guiTextured,
                            INDEX_LOCATION,
                            posX - 2,
                            posY - 2,
                            316,
                            0,
                            140,
                            22,
                            512,
                            256);
                }
            }

            @Override
            public void renderForeground(Minecraft minecraft, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, int posX, int posY) {
                super.renderForeground(minecraft, guiGraphics, mouseX, mouseY, partialTick, posX, posY);
                Font font = minecraft.font;
                guiGraphics.drawString(font,
                        Language.getInstance().getVisualOrder(this.displayName),
                        posX + 70 - font.width(this.displayName) / 2,
                        posY,
                        0,
                        false);
                guiGraphics.drawSpecial((MultiBufferSource multiBufferSource) -> {
                    font.drawInBatch8xOutline(this.collection.getVisualOrderText(),
                            posX + 70 - font.width(this.collection) / 2,
                            posY + 10,
                            0xFFC700,
                            0,
                            guiGraphics.pose().last().pose(),
                            multiBufferSource,
                            0xF000F0);
                });
            }

            @Override
            public boolean mouseClicked(Screen screen, int mouseX, int mouseY, int buttonId) {
                screen.minecraft.setScreen(new ItemsIndexViewScreen(screen,
                        ((IndexViewScreen) screen).fromInventory,
                        this.items));
                screen.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            }
        }
    }
}