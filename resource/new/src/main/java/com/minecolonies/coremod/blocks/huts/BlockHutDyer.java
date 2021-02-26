package com.minecolonies.coremod.blocks.huts;



import org.jetbrains.annotations.NotNull;

/**
 * Hut for the dyer. No different from {@link AbstractBlockHut}
 */
public class BlockHutDyer extends AbstractBlockHut<BlockHutDyer>
{
    @NotNull
    @Override
    public String getName()
    {
        return "blockhutdyer";
    }

    @Override
    public BuildingEntry getBuildingEntry()
    {
        return ModBuildings.dyer;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void checkResearch(final IColonyView colony)
    {
        checkResearch(colony, ResearchInitializer.DYER_RESEARCH);
    }
}
