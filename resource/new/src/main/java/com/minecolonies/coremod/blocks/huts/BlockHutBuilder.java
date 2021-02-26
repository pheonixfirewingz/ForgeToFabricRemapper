package com.minecolonies.coremod.blocks.huts;

import org.jetbrains.annotations.NotNull;

/**
 * Hut for the builder. No different from {@link AbstractBlockHut}
 */
public class BlockHutBuilder extends AbstractBlockHut<BlockHutBuilder>
{
    public BlockHutBuilder()
    {
        //No different from Abstract parent
        super();
    }

    @NotNull
    @Override
    public String getName()
    {
        return "blockhutbuilder";
    }

    @Override
    public BuildingEntry getBuildingEntry()
    {
        return ModBuildings.builder;
    }
}