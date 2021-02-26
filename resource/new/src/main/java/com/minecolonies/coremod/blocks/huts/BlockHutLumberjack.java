package com.minecolonies.coremod.blocks.huts;

import org.jetbrains.annotations.NotNull;

/**
 * Hut for the lumberjack. No different from {@link AbstractBlockHut}
 */
public class BlockHutLumberjack extends AbstractBlockHut<BlockHutLumberjack>
{
    public BlockHutLumberjack()
    {
        //No different from Abstract parent
        super();
    }

    @NotNull
    @Override
    public String getName()
    {
        return "blockhutlumberjack";
    }

    @Override
    public BuildingEntry getBuildingEntry()
    {
        return ModBuildings.lumberjack;
    }
}
