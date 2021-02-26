package com.minecolonies.coremod.blocks.huts;



import org.jetbrains.annotations.NotNull;

/**
 * Hut for the fletcher. No different from {@link AbstractBlockHut}
 */
public class BlockHutFletcher extends AbstractBlockHut<BlockHutFletcher>
{
    @NotNull
    @Override
    public String getName()
    {
        return "blockhutfletcher";
    }

    @Override
    public BuildingEntry getBuildingEntry()
    {
        return ModBuildings.fletcher;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void checkResearch(final IColonyView colony)
    {
        checkResearch(colony, ResearchInitializer.FLETCHER_RESEARCH);
    }
}
