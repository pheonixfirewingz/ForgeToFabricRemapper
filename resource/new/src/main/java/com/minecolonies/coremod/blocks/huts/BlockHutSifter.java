package com.minecolonies.coremod.blocks.huts;



import org.jetbrains.annotations.NotNull;

/**
 * Hut for the sifter. No different from {@link AbstractBlockHut}
 */
public class BlockHutSifter extends AbstractBlockHut<BlockHutSifter>
{
    @NotNull
    @Override
    public String getName()
    {
        return "blockhutsifter";
    }

    @Override
    public BuildingEntry getBuildingEntry()
    {
        return ModBuildings.sifter;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void checkResearch(final IColonyView colony)
    {
        checkResearch(colony, ResearchInitializer.SIFTER_RESEARCH);
    }
}
