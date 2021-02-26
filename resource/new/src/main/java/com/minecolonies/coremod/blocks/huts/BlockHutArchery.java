package com.minecolonies.coremod.blocks.huts;



import org.jetbrains.annotations.NotNull;

/**
 * Block of the Archers trainings camp.
 */
public class BlockHutArchery extends AbstractBlockHut<BlockHutArchery>
{
    @NotNull
    @Override
    public String getName()
    {
        return "blockhutarchery";
    }

    @Override
    public BuildingEntry getBuildingEntry()
    {
        return ModBuildings.archery;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void checkResearch(final IColonyView colony)
    {
        checkResearch(colony, ResearchInitializer.ARCHERY_RESEARCH);
    }
}
