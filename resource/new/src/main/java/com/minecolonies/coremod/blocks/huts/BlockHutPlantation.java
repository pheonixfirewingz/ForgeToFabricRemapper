package com.minecolonies.coremod.blocks.huts;



import org.jetbrains.annotations.NotNull;

/**
 * Hut for the plantation. No different from {@link AbstractBlockHut}
 */

public class BlockHutPlantation extends AbstractBlockHut<BlockHutPlantation>
{
    @NotNull
    @Override
    public String getName()
    {
        return "blockhutplantation";
    }

    @Override
    public BuildingEntry getBuildingEntry()
    {
        return ModBuildings.plantation;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void checkResearch(final IColonyView colony)
    {
        checkResearch(colony, ResearchInitializer.PLANTATION_RESEARCH);
    }
}
