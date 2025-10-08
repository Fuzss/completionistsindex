package fuzs.completionistsindex.config;

import fuzs.puzzleslib.api.config.v3.Config;
import fuzs.puzzleslib.api.config.v3.ConfigCore;
import fuzs.puzzleslib.api.config.v3.serialization.ConfigDataSet;
import fuzs.puzzleslib.api.config.v3.serialization.KeyedValueProvider;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public class ClientConfig implements ConfigCore {
    @Config(description = "Choose which screens to add the Completionist's Index button to.")
    public IndexButtonScreen indexButtonScreen = IndexButtonScreen.PAUSE_MENU;
    @Config(
            name = "indexed_items",
            description = "A list for overriding all items in the index, no other items are included."
    )
    List<String> indexedItemsRaw = KeyedValueProvider.tagAppender(Registries.ITEM).asStringList();
    @Config(
            name = "unobtainable_items", description = {
            "Add items to this list that should be excluded from the index, intended for creative-only items such as spawn eggs.",
            ConfigDataSet.CONFIG_DESCRIPTION
    }
    )
    List<String> unobtainableItemsRaw = Util.make(new ArrayList<>(KeyedValueProvider.tagAppender(Registries.ITEM)
            .add(Items.BEDROCK,
                    Items.BUDDING_AMETHYST,
                    Items.CHORUS_PLANT,
                    Items.END_PORTAL_FRAME,
                    Items.FARMLAND,
                    Items.FROGSPAWN,
                    Items.INFESTED_STONE,
                    Items.INFESTED_COBBLESTONE,
                    Items.INFESTED_STONE_BRICKS,
                    Items.INFESTED_CHISELED_STONE_BRICKS,
                    Items.INFESTED_CRACKED_STONE_BRICKS,
                    Items.INFESTED_MOSSY_STONE_BRICKS,
                    Items.INFESTED_DEEPSLATE,
                    Items.REINFORCED_DEEPSLATE,
                    Items.SPAWNER,
                    Items.BARRIER,
                    Items.COMMAND_BLOCK,
                    Items.CHAIN_COMMAND_BLOCK,
                    Items.REPEATING_COMMAND_BLOCK,
                    Items.COMMAND_BLOCK_MINECART,
                    Items.PETRIFIED_OAK_SLAB,
                    Items.PLAYER_HEAD,
                    Items.STRUCTURE_BLOCK,
                    Items.STRUCTURE_VOID,
                    Items.TRIAL_SPAWNER,
                    Items.VAULT)
            .asStringList()), (List<String> list) -> {
        list.add("minecraft:*_spawn_egg");
    });
    @Config(
            name = "hidden_creative_tabs", description = {
            "Creative mode tabs containing items inaccessible in survival that should be excluded from the item groups, such as the operator items tab. ",
            ConfigDataSet.CONFIG_DESCRIPTION
    }
    )
    List<String> hiddenCreativeTabsRaw = KeyedValueProvider.tagAppender(Registries.CREATIVE_MODE_TAB)
            .addKey(CreativeModeTabs.OP_BLOCKS)
            .asStringList();

    public ConfigDataSet<Item> indexedItems;
    public ConfigDataSet<Item> unobtainableItems;
    public ConfigDataSet<CreativeModeTab> hiddenCreativeTabs;

    @Override
    public void afterConfigReload() {
        this.indexedItems = ConfigDataSet.from(Registries.ITEM, this.indexedItemsRaw);
        this.unobtainableItems = ConfigDataSet.from(Registries.ITEM, this.unobtainableItemsRaw);
        this.hiddenCreativeTabs = ConfigDataSet.from(Registries.CREATIVE_MODE_TAB, this.hiddenCreativeTabsRaw);
    }

    public boolean filterItems(Item item) {
        if (this.indexedItems.isEmpty() || this.indexedItems.contains(item)) {
            return !this.unobtainableItems.contains(item);
        } else {
            return false;
        }
    }

    public enum IndexButtonScreen {
        PAUSE_MENU,
        INVENTORY_MENU,
        BOTH
    }
}
