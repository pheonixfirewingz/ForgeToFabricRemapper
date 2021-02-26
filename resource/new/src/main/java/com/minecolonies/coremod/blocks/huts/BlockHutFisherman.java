package com.minecolonies.coremod.blocks.huts;

import org.jetbrains.annotations.NotNull;

/**
 * Hut for the fisherman. No different from {@link AbstractBlockHut}
 */
public class BlockHutFisherman extends AbstractBlockHut<BlockHutFisherman>
{
    public BlockHutFisherman()
    {
        //No different from Abstract parent
        super();
    }

    @NotNull
    @Override
    public String getName()
    {
        return "blockhutfisherman";
    }

    @Override
    public BuildingEntry getBuildingEntry()
    {
        return ModBuildings.fisherman;
    }
}