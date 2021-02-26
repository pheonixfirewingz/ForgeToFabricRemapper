package com.minecolonies.coremod.colony.colony.buildings.modules;

import org.jetbrains.annotations.NotNull;

/**
 * The class of the citizen hut.
 */
public class HomeBuildingModule extends AbstractBuildingModule implements IDefinesCoreBuildingStatsModule
{
    @Override
    public IStat<Integer> getMaxInhabitants()
    {
        return (prev) -> Math.max(prev, building.getBuildingLevel());
    }

    /**
     * ClientSide representation of the building.
     */
    public static class View extends LivingBuildingView
    {
        /**
         * Creates an instance of the citizen hut window.
         *
         * @param c the colonyView.
         * @param l the position the hut is at.
         */
        public View(final IColonyView c, final BlockPos l)
        {
            super(c, l);
        }

        @NotNull
        @Override
        public Window getWindow()
        {
            return new WindowHutCitizen(this);
        }
    }
}
