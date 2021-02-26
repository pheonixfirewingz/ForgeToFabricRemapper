package com.minecolonies.coremod.blocks.huts;



import org.jetbrains.annotations.NotNull;

/**
 * Hut for the glassblower. No different from {@link AbstractBlockHut}
 */
public class BlockHutGlassblower extends AbstractBlockHut<BlockHutGlassblower>
{
    @NotNull
    @Override
    public String getName()
    {
        return "blockhutglassblower";
    }

    @Override
    public BuildingEntry getBuildingEntry()
    {
        return ModBuildings.glassblower;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void checkResearch(final IColonyView colony)
    {
        checkResearch(colony, ResearchInitializer.GLASSBLOWER_RESEARCH);
    }
}
