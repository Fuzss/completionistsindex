package fuzs.completionistsindex.client.gui.screens.index;

import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.achievement.StatsScreen;
import net.minecraft.stats.StatsCounter;
import org.jetbrains.annotations.Nullable;

/**
 * An implementation similar to vanilla's old {@code StatsUpdateListener} interface, whose functionality is now
 * restricted to instances of {@link StatsScreen}.
 */
public abstract class StatsUpdateListener extends StatsScreen {

    public StatsUpdateListener(@Nullable Screen lastScreen) {
        super(lastScreen, new StatsCounter());
    }

    @Override
    protected void init() {
        // NO-OP
    }

    @Override
    protected void repositionElements() {
        this.rebuildWidgets();
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }

    @Override
    public final void initLists() {
        // NO-OP
    }

    @Override
    public final void initButtons() {
        // NO-OP
    }

    @Override
    public final void setActiveList(@Nullable ObjectSelectionList<?> activeList) {
        // NO-OP
    }

    @Override
    public void onStatsUpdated() {
        // NO-OP
    }
}
