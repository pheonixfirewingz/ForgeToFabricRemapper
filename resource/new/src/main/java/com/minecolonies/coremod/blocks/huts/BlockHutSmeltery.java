package com.minecolonies.coremod.blocks.huts;



import org.jetbrains.annotations.NotNull;

/**
 * Hut for the Smeltery. No different from {@link AbstractBlockHut}
 */
public class BlockHutSmeltery extends AbstractBlockHut<BlockHutSmeltery>
{
    public BlockHutSmeltery()
    {
        //No different from Abstract parent
        super();
    }

    @NotNull
    @Override
    public String getName()
    {
        return "blockhutsmeltery";
    }

    @Override
    public BuildingEntry getBuildingEntry()
    {
        return ModBuildings.smeltery;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void checkResearch(final IColonyView colony)
    {
        checkResearch(colony, ResearchInitializer.SMELTERY_RESEARCH);
    }
}
