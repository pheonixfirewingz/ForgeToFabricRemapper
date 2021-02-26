package com.minecolonies.coremod.blocks.huts;



import org.jetbrains.annotations.NotNull;

/**
 * Hut for the StoneSmeltery. No different from {@link AbstractBlockHut}
 */
public class BlockHutStoneSmeltery extends AbstractBlockHut<BlockHutStoneSmeltery>
{
    @NotNull
    @Override
    public String getName()
    {
        return "blockhutstonesmeltery";
    }

    @Override
    public BuildingEntry getBuildingEntry()
    {
        return ModBuildings.stoneSmelter;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void checkResearch(final IColonyView colony)
    {
        checkResearch(colony, ResearchInitializer.STONESMELTERY_RESEARCH);
    }
}
