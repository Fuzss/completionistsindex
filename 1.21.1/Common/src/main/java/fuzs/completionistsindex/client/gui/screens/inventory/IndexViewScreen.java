package fuzs.completionistsindex.client.gui.screens.inventory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import fuzs.completionistsindex.CompletionistsIndex;
import fuzs.puzzleslib.api.client.gui.v2.components.SpritelessImageButton;
import fuzs.puzzleslib.api.core.v1.ModContainer;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.*;

public abstract class IndexViewScreen extends StatsUpdateListener {
   public static final ResourceLocation INDEX_LOCATION = CompletionistsIndex.id("textures/gui/index.png");
   private static final MutableComponent PREVIOUS_PAGE_COMPONENT = Component.translatable("createWorld.customize.custom.prev");
   private static final MutableComponent NEXT_PAGE_COMPONENT = Component.translatable("createWorld.customize.custom.next");

   private final boolean fromInventory;
   protected int leftPos;
   protected int topPos;
   private Button turnPageBackwards;
   private Button turnPageForwards;
   private static StatsSorting statsSorting = StatsSorting.CREATIVE;
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

   public Comparator<IndexViewPage.Entry> getComparator() {
      return statsSorting.getComparator();
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
      this.addRenderableWidget(new SpritelessImageButton(this.leftPos + 17, this.topPos + 11, 16, 13, 42, 202, 20, INDEX_LOCATION, 512, 256, button -> {
         this.onClose();
      })).setTooltip(Tooltip.create(CommonComponents.GUI_BACK));
      this.addRenderableWidget(new SpritelessImageButton(this.leftPos + 316 - 17 - 16, this.topPos + 11, 16, 13, 62, 202, 20, INDEX_LOCATION, 512, 256, button -> {
         statsSorting = statsSorting.cycle();
         button.setTooltip(Tooltip.create(statsSorting.getComponent()));
         this.rebuildPages();
      })).setTooltip(Tooltip.create(statsSorting.getComponent()));
      this.turnPageBackwards = this.addRenderableWidget(new SpritelessImageButton(this.leftPos + 27, this.topPos + 173, 18, 10, 1, 203, 20, INDEX_LOCATION, 512, 256, button -> {
         this.decrementPage();
      }));
      this.turnPageBackwards.setTooltip(Tooltip.create(PREVIOUS_PAGE_COMPONENT));
      this.turnPageForwards = this.addRenderableWidget(new SpritelessImageButton(this.leftPos + 316 - 27 - 18, this.topPos + 173, 18, 10, 21, 203, 20, INDEX_LOCATION, 512, 256, button -> {
         this.incrementPage();
      }));
      this.turnPageForwards.setTooltip(Tooltip.create(NEXT_PAGE_COMPONENT));
      this.setCurrentPage(this.currentPage);
   }

   @Override
   public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
      if (this.fromInventory) {
         this.renderTransparentBackground(guiGraphics);
      } else {
          super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
      }
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      guiGraphics.blit(INDEX_LOCATION, this.leftPos, this.topPos, 0, 0, 316, 198, 512, 256);
      guiGraphics.drawString(this.font, this.leftPageIndicator, this.leftPos + 82 - this.font.width(this.leftPageIndicator) / 2, this.topPos + 13, 0xB8A48A, false);
      guiGraphics.drawString(this.font, this.rightPageIndicator, this.leftPos + 233 - this.font.width(this.rightPageIndicator) / 2, this.topPos + 13, 0xB8A48A, false);
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
         this.renderPageHalf(guiGraphics, mouseX, mouseY, partialTick, this.screen.leftPos + 16, this.screen.topPos + 26, 0, 7);
         this.renderPageHalf(guiGraphics, mouseX, mouseY, partialTick, this.screen.leftPos + 167, this.screen.topPos + 26, 7, 14);
      }

      private void renderPageHalf(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, int startX, int startY, int startIndex, int endIndex) {
         guiGraphics.pose().pushPose();
         guiGraphics.pose().translate(startX, startY, 0.0F);
         for (int i = startIndex; i < endIndex; i++) {
            Entry entry = this.entries[i];
            if (entry == null) break;
            int mouseXOffset = mouseX - startX;
            int mouseYOffset = mouseY - startY - i % 7 * 21;
             entry.render(this.screen.minecraft, guiGraphics, mouseXOffset, mouseYOffset, partialTick);
            if (entry.tryRenderTooltip(guiGraphics, mouseXOffset, mouseYOffset)) {
               this.screen.tooltipLines = entry.getTooltipLines();
            }
            guiGraphics.pose().translate(0.0F, 21.0F, 0.0F);
         }
         guiGraphics.pose().popPose();
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

      public static Entry modItemEntry(String modId, List<ItemStack> items, StatsCounter statsCounter, Font font) {
         if (items.isEmpty()) throw new IllegalArgumentException("items cannot be empty");
         Component modName;
         if (modId.equals(ModsIndexViewScreen.ALL_ITEMS_PLACEHOLDER)) {
            modName = Component.translatable("gui.all");
         } else {
            // apparently mods on Fabric are not forced to use their mod id when registering content, so we might not find the mod and its name after all
            // just use the prettified namespace then
            modName = Component.literal(ModLoaderEnvironment.INSTANCE.getModContainer(modId).map(ModContainer::getDisplayName).orElse(prettifyModId(modId)));
         }
         ItemStack displayItem = items.stream().skip((int) (Math.random() * items.size())).findAny().orElseThrow();
         long collectedCount = items.stream().filter(stack -> {
            int pickedUp = statsCounter.getValue(Stats.ITEM_PICKED_UP, stack.getItem());
            int crafted = statsCounter.getValue(Stats.ITEM_CRAFTED, stack.getItem());
            return pickedUp + crafted > 0;
         }).count();
         boolean collected = collectedCount == items.size();
         float collectionProgress = collectedCount / (float) items.size();
         Component tooltipComponent = Component.empty().append(modName).append(Component.literal(" (" + PERCENTAGE_FORMAT.format(collectionProgress * 100.0F) + "%)").withStyle(ChatFormatting.GOLD));
         return new ModItemEntry(displayItem, formatDisplayName(font, modName, collected), collected, List.of(tooltipComponent), Component.literal(collectedCount + "/" + items.size()), collectionProgress, items);
      }

      private static String prettifyModId(String modId) {
         StringJoiner joiner = new StringJoiner(" ");
         String[] parts = modId.split("_");
         for (String part : parts) {
            if (!part.isEmpty()) {
               joiner.add(Character.toUpperCase(part.charAt(0)) + part.substring(1).toLowerCase(Locale.ROOT));
            }
         }
         return joiner.toString();
      }

      public static Entry statsItemEntry(ItemStack itemStack, StatsCounter statsCounter, Font font) {
         int pickedUp = statsCounter.getValue(Stats.ITEM_PICKED_UP, itemStack.getItem());
         int crafted = statsCounter.getValue(Stats.ITEM_CRAFTED, itemStack.getItem());
         boolean collected = pickedUp > 0 || crafted > 0;
         Component displayName = itemStack.getItem().getName(itemStack);
         FormattedText formattedName = formatDisplayName(font, displayName, collected);
         List<Component> lines = Lists.newArrayList();
         lines.add(Component.empty().append(itemStack.getItem().getName(itemStack)).withStyle(itemStack.getRarity().color()));
         itemStack.getItem().appendHoverText(itemStack, Item.TooltipContext.EMPTY, lines, TooltipFlag.NORMAL);
         if (pickedUp > 0) {
            lines.add(Component.literal(String.valueOf(pickedUp)).append(" ").append(Component.translatable("stat_type.minecraft.picked_up")).withStyle(ChatFormatting.BLUE));
         }
         if (crafted > 0) {
            lines.add(Component.literal(String.valueOf(crafted)).append(" ").append(Component.translatable("stat_type.minecraft.crafted")).withStyle(ChatFormatting.BLUE));
         }
         return new StatsItemEntry(itemStack, formattedName, collected, ImmutableList.copyOf(lines));
      }

      private static FormattedText formatDisplayName(Font font, Component displayName, boolean collected) {
         Style style = Style.EMPTY.withColor(collected ? 0x4BA52F : ChatFormatting.BLACK.getColor());
         FormattedText formattedText;
         if (font.width(displayName) > 95) {
            formattedText = FormattedText.composite(font.getSplitter().headByWidth(displayName, 95 - font.width("..."), style), Component.literal("...").withStyle(style));
         } else {
            formattedText = Component.empty().append(displayName).withStyle(style);
         }
         return formattedText;
      }

      public static abstract class Entry {
         final ItemStack item;
         final FormattedText displayName;
         private final boolean collected;
         private final List<Component> tooltipLines;

         private Entry(ItemStack item, FormattedText displayName, boolean collected, List<Component> tooltipLines) {
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

         public void render(Minecraft minecraft, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
            this.renderForeground(minecraft, guiGraphics, mouseX, mouseY, partialTick);
         }

         public boolean tryRenderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
            if (this.isHovering(0, 0, 18, 18, mouseX, mouseY)) {
               AbstractContainerScreen.renderSlotHighlight(guiGraphics, 1, 1, 0);
               return true;
            }
            return false;
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

         public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            guiGraphics.blit(INDEX_LOCATION, 0, 0, 120, 208, 18, 18, 512, 256);
            guiGraphics.blit(INDEX_LOCATION, 124, 4, 120 + (this.collected ? 10 : 0), 198, 10, 10, 512, 256);
         }

         public void renderForeground(Minecraft minecraft, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            guiGraphics.renderItem(this.item, 1, 1);
         }
      }

      private static class StatsItemEntry extends Entry {

         public StatsItemEntry(ItemStack item, FormattedText displayName, boolean collected, List<Component> tooltipLines) {
            super(item, displayName, collected, tooltipLines);
         }

         @SuppressWarnings("unchecked")
         @Override
         public <T extends Comparable<? super T>> T toComparableKey() {
            return (T) BuiltInRegistries.ITEM.getKey(this.item.getItem()).getPath();
         }

         @Override
         public void renderForeground(Minecraft minecraft, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            super.renderForeground(minecraft, guiGraphics, mouseX, mouseY, partialTick);
            guiGraphics.drawString(minecraft.font, Language.getInstance().getVisualOrder(this.displayName), 23, 5, 0x000000, false);
         }
      }

      public static class ModItemEntry extends Entry {
         private final Component collection;
         private final float collectionProgress;
         private final List<ItemStack> items;

         public ModItemEntry(ItemStack item, FormattedText displayName, boolean collected, List<Component> tooltipLines, Component collection, float collectionProgress, List<ItemStack> items) {
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
         public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            guiGraphics.blit(INDEX_LOCATION, 24, 11, 140, 198, 91, 5, 512, 256);
            guiGraphics.blit(INDEX_LOCATION, 24, 11, 140, 203, (int) (91 * this.collectionProgress), 5, 512, 256);
            if (this.isMouseOver(mouseX, mouseY)) {
               guiGraphics.blit(INDEX_LOCATION, -2, -2, 316, 0, 140, 22, 512, 256);
            }
         }

         @Override
         public void renderForeground(Minecraft minecraft, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            super.renderForeground(minecraft, guiGraphics, mouseX, mouseY, partialTick);
            Font font = minecraft.font;
            int posX = 70 - font.width(this.displayName) / 2;
            guiGraphics.drawString(font, Language.getInstance().getVisualOrder(this.displayName), posX, 0, 0x000000, false);
            posX = 70 - font.width(this.collection) / 2;
            guiGraphics.drawString(font, this.collection, posX - 1, 10, 0x000000, false);
            guiGraphics.drawString(font, this.collection, posX, 10 - 1, 0x000000, false);
            guiGraphics.drawString(font, this.collection, posX, 10 + 1, 0x000000, false);
            guiGraphics.drawString(font, this.collection, posX + 1, 10, 0x000000, false);
            guiGraphics.drawString(font, this.collection, posX, 10, 0xFFC700, false);
         }

         @Override
         public boolean mouseClicked(Screen screen, int mouseX, int mouseY, int buttonId) {
            screen.minecraft.setScreen(new ItemsIndexViewScreen(screen, ((IndexViewScreen) screen).fromInventory, this.items));
            screen.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return true;
         }
      }
   }
}