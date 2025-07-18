package fuzs.completionistsindex.client.gui.screens.index;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Ordering;
import fuzs.completionistsindex.CompletionistsIndex;
import fuzs.completionistsindex.client.gui.components.index.IndexViewEntry;
import fuzs.completionistsindex.config.ClientConfig;
import fuzs.puzzleslib.api.core.v1.ModContainer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public enum IndexGroup implements SortProvider<IndexGroup> {
    CREATIVE {
        @Override
        public Comparator<IndexViewEntry<?>> getComparator() {
            return Ordering.allEqual()::compare;
        }

        @Override
        SequencedMap<Component, List<ItemStack>> getGroups() {
            SequencedMap<Component, List<ItemStack>> groups = new LinkedHashMap<>();
            for (CreativeModeTab creativeModeTab : BuiltInRegistries.CREATIVE_MODE_TAB.stream().toList()) {
                if (creativeModeTab.getType() == CreativeModeTab.Type.CATEGORY && !CompletionistsIndex.CONFIG.get(
                        ClientConfig.class).hiddenCreativeTabs.contains(creativeModeTab)) {
                    List<ItemStack> items = this.getDisplayItems(creativeModeTab);
                    if (!items.isEmpty()) {
                        groups.put(creativeModeTab.getDisplayName(), items);
                    }
                }
            }

            return groups;
        }
    },
    MODS {
        @Override
        public Comparator<IndexViewEntry<?>> getComparator() {
            return Comparator.comparing(IndexViewEntry::toComparableKey);
        }

        @Override
        SequencedMap<Component, List<ItemStack>> getGroups() {
            return this.getAllItems().stream().collect(Collectors.groupingBy((ItemStack itemStack) -> {
                String modId = BuiltInRegistries.ITEM.getKey(itemStack.getItem()).getNamespace();
                return Component.literal(ModContainer.getDisplayName(modId));
            }, LinkedHashMap::new, Collectors.toList()));
        }
    };

    private static final IndexGroup[] VALUES = IndexGroup.values();

    private final Component component;

    IndexGroup() {
        this.component = Component.translatable(
                CompletionistsIndex.MOD_ID + ".gui.index.group." + this.name().toLowerCase(Locale.ROOT));
    }

    @Override
    public IndexGroup cycle() {
        return VALUES[(this.ordinal() + 1) % VALUES.length];
    }

    @Override
    public Component getComponent() {
        return this.component;
    }

    abstract SequencedMap<Component, List<ItemStack>> getGroups();

    public final Map<Component, List<ItemStack>> getDisplayGroups() {
        SequencedMap<Component, List<ItemStack>> groups = this.getGroups();
        if (groups.size() > 1) {
            groups.putFirst(Component.translatable("gui.all"), this.getAllItems());
        }
        return ImmutableMap.copyOf(groups);
    }

    List<ItemStack> getAllItems() {
        return this.getDisplayItems(CreativeModeTabs.searchTab());
    }

    List<ItemStack> getDisplayItems(CreativeModeTab creativeModeTab) {
        return creativeModeTab.getDisplayItems()
                .stream()
                .map(ItemStack::getItem)
                .distinct()
                .filter(CompletionistsIndex.CONFIG.get(ClientConfig.class)::filterItems)
                .map(ItemStack::new)
                .toList();
    }
}
