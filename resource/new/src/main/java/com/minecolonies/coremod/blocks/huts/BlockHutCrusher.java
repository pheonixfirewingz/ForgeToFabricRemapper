package com.minecolonies.coremod.blocks.huts;



import org.jetbrains.annotations.NotNull;

/**
 * Hut for the crusher. No different from {@link AbstractBlockHut}
 */
public class BlockHutCrusher extends AbstractBlockHut<BlockHutCrusher>
{
    @NotNull
    @Override
    public String getName()
    {
        return "blockhutcrusher";
    }

    @Override
    public BuildingEntry getBuildingEntry()
    {
        return ModBuildings.crusher;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void checkResearch(final IColonyView colony)
    {
        checkResearch(colony, ResearchInitializer.CRUSHER_RESEARCH);
    }
}
