package com.minecolonies.coremod.blocks.huts;

import org.jetbrains.annotations.NotNull;

/**
 * Hut for the citizen. No different from {@link AbstractBlockHut}
 */
public class BlockHutCitizen extends AbstractBlockHut<BlockHutCitizen>
{
    public BlockHutCitizen()
    {
        //No different from Abstract parent
        super();
    }

    @NotNull
    @Override
    public String getName()
    {
        return "blockhutcitizen";
    }

    @Override
    public BuildingEntry getBuildingEntry()
    {
        return ModBuildings.home;
    }
}