package com.minecolonies.coremod.blocks.huts;



import org.jetbrains.annotations.NotNull;

/**
 * Hut for the library. No different from {@link AbstractBlockHut}
 */
public class BlockHutLibrary extends AbstractBlockHut<BlockHutLibrary>
{
    @NotNull
    @Override
    public String getName()
    {
        return "blockhutlibrary";
    }

    @Override
    public BuildingEntry getBuildingEntry()
    {
        return ModBuildings.library;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void checkResearch(final IColonyView colony)
    {
        checkResearch(colony, ResearchInitializer.LIBRARY_RESEARCH);
    }
}