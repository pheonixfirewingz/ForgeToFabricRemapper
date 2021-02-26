package com.minecolonies.coremod.blocks.huts;



import org.jetbrains.annotations.NotNull;

public class BlockHutFlorist extends AbstractBlockHut<BlockHutFlorist>
{
    @NotNull
    @Override
    public String getName()
    {
        return "blockhutflorist";
    }

    @Override
    public BuildingEntry getBuildingEntry()
    {
        return ModBuildings.florist;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void checkResearch(final IColonyView colony)
    {
        checkResearch(colony, ResearchInitializer.FLORIST_RESEARCH);
    }
}
