package com.minecolonies.coremod.blocks.huts;

import org.jetbrains.annotations.NotNull;

/**
 * Hut for the lumberjack. No different from {@link AbstractBlockHut}
 */
public class BlockHutCook extends AbstractBlockHut<BlockHutCook>
{
    public BlockHutCook()
    {
        //No different from Abstract parent
        super();
    }

    @NotNull
    @Override
    public String getName()
    {
        return "blockhutcook";
    }

    @Override
    public BuildingEntry getBuildingEntry()
    {
        return ModBuildings.cook;
    }
}
