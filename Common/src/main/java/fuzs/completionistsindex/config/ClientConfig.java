package fuzs.completionistsindex.config;

import fuzs.puzzleslib.config.ConfigCore;
import fuzs.puzzleslib.config.annotation.Config;
import fuzs.puzzleslib.config.serialization.ConfigDataSet;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;

import java.util.List;

public class ClientConfig implements ConfigCore {
    @Config(description = "Choose which screens to add the Completionist's Index button to.")
    public IndexButtonScreen indexButtonScreen = IndexButtonScreen.PAUSE_MENU;
    @Config(name = "blacklist", description = {"Add items to this list that should be excluded from the index, intended for creative-only items such as spawn eggs.", ConfigDataSet.CONFIG_DESCRIPTION})
    List<String> blacklistRaw = List.of("minecraft:*_spawn_egg", "minecraft:bedrock", "minecraft:budding_amethyst", "minecraft:chorus_plant", "minecraft:end_portal_frame", "minecraft:farmland", "minecraft:frogspawn", "minecraft:infested_stone", "minecraft:infested_cobblestone", "minecraft:infested_stone_bricks", "minecraft:infested_cracked_stone_bricks", "minecraft:infested_mossy_stone_bricks", "minecraft:infested_chiseled_stone_bricks", "minecraft:infested_deepslate", "minecraft:reinforced_deepslate", "minecraft:spawner", "minecraft:barrier", "minecraft:bundle", "minecraft:command_block", "minecraft:chain_command_block", "minecraft:repeating_command_block", "minecraft:jigsaw", "minecraft:light", "minecraft:command_block_minecart", "minecraft:petrified_oak_slab", "minecraft:player_head", "minecraft:structure_block", "minecraft:structure_void");

    public ConfigDataSet<Item> blacklist;

    @Override
    public void afterConfigReload() {
        this.blacklist = ConfigDataSet.of(Registry.ITEM_REGISTRY, this.blacklistRaw);
    }

    public enum IndexButtonScreen {
        PAUSE_MENU, INVENTORY_MENU, BOTH
    }
}
