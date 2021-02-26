package com.minecolonies.coremod.blocks.huts;



import org.jetbrains.annotations.NotNull;

/**
 * Hut for the stone mason. No different from {@link AbstractBlockHut}
 */
public class BlockHutStonemason extends AbstractBlockHut<BlockHutStonemason>
{
    @NotNull
    @Override
    public String getName()
    {
        return "blockhutstonemason";
    }

    @Override
    public BuildingEntry getBuildingEntry()
    {
        return ModBuildings.stoneMason;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void checkResearch(final IColonyView colony)
    {
        checkResearch(colony, ResearchInitializer.STONEMASON_RESEARCH);
    }
}
