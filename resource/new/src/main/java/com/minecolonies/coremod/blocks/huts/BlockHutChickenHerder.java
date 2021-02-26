package com.minecolonies.coremod.blocks.huts;

import org.jetbrains.annotations.NotNull;

/**
 * Hut for the shepherd. No different from {@link AbstractBlockHut}
 */
public class BlockHutChickenHerder extends AbstractBlockHut<BlockHutChickenHerder>
{
    public BlockHutChickenHerder()
    {
        //No different from Abstract parent
        super();
    }

    @NotNull
    @Override
    public String getName()
    {
        return "blockhutchickenherder";
    }

    @Override
    public BuildingEntry getBuildingEntry()
    {
        return ModBuildings.chickenHerder;
    }
}
