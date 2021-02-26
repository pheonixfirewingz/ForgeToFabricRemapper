package com.minecolonies.coremod.blocks.huts;



import org.jetbrains.annotations.NotNull;

/**
 * Hut for the school. No different from {@link AbstractBlockHut}
 */
public class BlockHutSchool extends AbstractBlockHut<BlockHutSchool>
{
    public BlockHutSchool()
    {
        //No different from Abstract parent
        super();
    }

    @NotNull
    @Override
    public String getName()
    {
        return "blockhutschool";
    }

    @Override
    public BuildingEntry getBuildingEntry()
    {
        return ModBuildings.school;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void checkResearch(final IColonyView colony)
    {
        checkResearch(colony, ResearchInitializer.SCHOOL_RESEARCH);
    }
}