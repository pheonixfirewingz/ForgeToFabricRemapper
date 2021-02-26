package com.minecolonies.coremod.blocks.huts;


/**
 * Hut for the beekeeper. No different from {@link AbstractBlockHut}
 */
public class BlockHutBeekeeper extends AbstractBlockHut<BlockHutBeekeeper>
{
    /**
     * Method to return the name of the block.
     *
     * @return Name of the block.
     */
    @Override
    public String getName()
    {
        return "blockhutbeekeeper";
    }

    /**
     * Method to get the building registry entry.
     *
     * @return The building entry.
     */
    @Override
    public BuildingEntry getBuildingEntry()
    {
        return ModBuildings.beekeeper;
    }
}
