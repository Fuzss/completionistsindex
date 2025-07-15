package fuzs.completionistsindex.client.gui.screens.index;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Ordering;
import fuzs.completionistsindex.CompletionistsIndex;
import fuzs.completionistsindex.client.gui.components.index.IndexViewEntry;
import fuzs.completionistsindex.config.ClientConfig;
import fuzs.puzzleslib.api.core.v1.ModContainer;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
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
        SequencedMap<Component, List<ItemStack>> getGroups(List<ItemStack> allItems) {
            SequencedMap<Component, List<ItemStack>> groups = new LinkedHashMap<>();
            for (CreativeModeTab creativeModeTab : BuiltInRegistries.CREATIVE_MODE_TAB.stream().toList()) {
                if (creativeModeTab.getType() == CreativeModeTab.Type.CATEGORY &&
                        !CompletionistsIndex.CONFIG.get(ClientConfig.class).hiddenCreativeTabs.contains(creativeModeTab)) {
                    List<ItemStack> items = getDisplayItems(creativeModeTab);
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
        SequencedMap<Component, List<ItemStack>> getGroups(List<ItemStack> items) {
            return items.stream().collect(Collectors.groupingBy((ItemStack itemStack) -> {
                String s = BuiltInRegistries.ITEM.getKey(itemStack.getItem()).getNamespace();
                return Component.literal(getModDisplayName(s));
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

    @Override
    public Comparator<IndexViewEntry> getComparator() {
        return switch (this) {
            case CREATIVE -> Ordering.allEqual()::compare;
            case MODS -> Comparator.comparing(IndexViewEntry::toComparableKey);
        };
    }

    abstract SequencedMap<Component, List<ItemStack>> getGroups(List<ItemStack> items);

    public final Map<Component, List<ItemStack>> getGroups() {
        List<ItemStack> items = getDisplayItems(CreativeModeTabs.searchTab());
        SequencedMap<Component, List<ItemStack>> groups = this.getGroups(items);
        if (groups.size() > 1) {
            groups.putFirst(Component.translatable("gui.all"),
                    items != null ? items : getDisplayItems(CreativeModeTabs.searchTab()));
        }
        return ImmutableMap.copyOf(groups);
    }

    static List<ItemStack> getDisplayItems(CreativeModeTab creativeModeTab) {
        return creativeModeTab.getDisplayItems()
                .stream()
                .map(ItemStack::getItem)
                .distinct()
                .filter(CompletionistsIndex.CONFIG.get(ClientConfig.class)::filterItems)
                .map(ItemStack::new)
                .toList();
    }

    static String getModDisplayName(String modId) {
        return ModLoaderEnvironment.INSTANCE.getModContainer(modId)
                .map(ModContainer::getDisplayName)
                .orElse(createDisplayNameFromModId(modId));
    }

    static String createDisplayNameFromModId(String modId) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        String[] parts = modId.split("_");
        for (String part : parts) {
            if (!part.isEmpty()) {
                stringJoiner.add(Character.toUpperCase(part.charAt(0)) + part.substring(1).toLowerCase(Locale.ROOT));
            }
        }
        return stringJoiner.toString();
    }
}
