package com.minecolonies.coremod.blocks.huts;

import org.jetbrains.annotations.NotNull;

/**
 * HutBlock for the Tavern
 */
public class BlockHutTavern extends AbstractBlockHut<BlockHutTavern>
{
    /**
     * Block name
     */
    public static final String BLOCKHUT_TAVERN = "blockhuttavern";

    @NotNull
    @Override
    public String getName()
    {
        return BLOCKHUT_TAVERN;
    }

    @Override
    public BuildingEntry getBuildingEntry()
    {
        return ModBuildings.tavern;
    }
}
