package com.minecolonies.coremod.blocks.huts;



import org.jetbrains.annotations.NotNull;

/**
 * Hut for the sawmill. No different from {@link AbstractBlockHut}
 */
public class BlockHutSawmill extends AbstractBlockHut<BlockHutSawmill>
{
    @NotNull
    @Override
    public String getName()
    {
        return "blockhutsawmill";
    }

    @Override
    public BuildingEntry getBuildingEntry()
    {
        return ModBuildings.sawmill;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void checkResearch(final IColonyView colony)
    {
        checkResearch(colony, ResearchInitializer.SAWMILL_RESEARCH);
    }
}
