package com.minecolonies.coremod.colony.colony.buildings.views;


/**
 * An Empty implementation of {@link IBuildingView}. Currently used for Stash.
 */
public class EmptyView extends AbstractBuildingView
{
    /**
     * Instantiates the view of the building.
     *
     * @param c the colonyView.
     * @param l the location of the block.
     */
    public EmptyView(final IColonyView c, final BlockPos l)
    {
        super(c, l);
    }
}
