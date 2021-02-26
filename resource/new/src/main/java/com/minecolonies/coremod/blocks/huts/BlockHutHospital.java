package com.minecolonies.coremod.blocks.huts;



import org.jetbrains.annotations.NotNull;

/**
 * Hut for the hospital. No different from {@link AbstractBlockHut}
 */
public class BlockHutHospital extends AbstractBlockHut<BlockHutHospital>
{
    public BlockHutHospital()
    {
        //No different from Abstract parent
        super();
    }

    @NotNull
    @Override
    public String getName()
    {
        return "blockhuthospital";
    }

    @Override
    public BuildingEntry getBuildingEntry()
    {
        return ModBuildings.hospital;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void checkResearch(final IColonyView colony)
    {
        checkResearch(colony, ResearchInitializer.HOSPITAL_RESEARCH);
    }
}
