package com.minecolonies.coremod.blocks.huts;



import org.jetbrains.annotations.NotNull;

/**
 * Block of the Barracks.
 */
public class BlockHutBarracks extends AbstractBlockHut<BlockHutBarracks>
{
    /**
     * Default constructor.
     */
    public BlockHutBarracks()
    {
        //No different from Abstract parent
        super();
    }

    @NotNull
    @Override
    public String getName()
    {
        return "blockhutbarracks";
    }

    @Override
    public BuildingEntry getBuildingEntry()
    {
        return ModBuildings.barracks;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void checkResearch(final IColonyView colony)
    {
        checkResearch(colony, ResearchInitializer.BARRACKS_RESEARCH);
    }
}
