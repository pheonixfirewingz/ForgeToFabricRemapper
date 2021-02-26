package com.minecolonies.coremod.blocks.huts;



import org.jetbrains.annotations.NotNull;

public class BlockHutComposter extends AbstractBlockHut<BlockHutComposter>
{

    @NotNull
    @Override
    public String getName()
    {
        return "blockhutcomposter";
    }

    @Override
    public BuildingEntry getBuildingEntry()
    {
        return ModBuildings.composter;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void checkResearch(final IColonyView colony)
    {
        checkResearch(colony, ResearchInitializer.COMPOSTER_RESEARCH);
    }
}
