package com.minecolonies.coremod.blocks.huts;

import org.jetbrains.annotations.NotNull;

/**
 * Hut for the bakery. No different from {@link AbstractBlockHut}
 */
public class BlockHutBaker extends AbstractBlockHut<BlockHutBaker>
{
    public BlockHutBaker()
    {
        //No different from Abstract parent
        super();
    }

    @NotNull
    @Override
    public String getName()
    {
        return "blockhutbaker";
    }

    @Override
    public BuildingEntry getBuildingEntry()
    {
        return ModBuildings.bakery;
    }
}
