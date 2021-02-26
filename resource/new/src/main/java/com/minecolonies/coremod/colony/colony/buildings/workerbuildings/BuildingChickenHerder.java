package com.minecolonies.coremod.colony.colony.buildings.workerbuildings;

import org.jetbrains.annotations.NotNull;

/**
 * Creates a new building for the Chicken Herder.
 */
public class BuildingChickenHerder extends AbstractBuildingHerder
{
    /**
     * Description of the job executed in the hut.
     */
    private static final String JOB = "chickenherder";

    /**
     * Max building level of the hut.
     */
    private static final int MAX_BUILDING_LEVEL = 5;

    /**
     * Instantiates the building.
     *
     * @param c the colony.
     * @param l the location.
     */
    public BuildingChickenHerder(final IColony c, final BlockPos l)
    {
        super(c, l);
    }

    @NotNull
    @Override
    public String getSchematicName()
    {
        return JOB;
    }

    @Override
    public int getMaxBuildingLevel()
    {
        return MAX_BUILDING_LEVEL;
    }

    @NotNull
    @Override
    public String getJobName()
    {
        return JOB;
    }

    @NotNull
    @Override
    public Skill getPrimarySkill()
    {
        return Skill.Adaptability;
    }

    @NotNull
    @Override
    public Skill getSecondarySkill()
    {
        return Skill.Agility;
    }

    @NotNull
    @Override
    public IJob<?> createJob(final ICitizenData citizen)
    {
        return new JobChickenHerder(citizen);
    }

    @Override
    public BuildingEntry getBuildingRegistryEntry()
    {
        return ModBuildings.chickenHerder;
    }

    /**
     * ClientSide representation of the building.
     */
    public static class View extends AbstractBuildingHerder.View
    {
        /**
         * Instantiates the view of the building.
         *
         * @param c the colonyView.
         * @param l the location of the block.
         */
        public View(final IColonyView c, final BlockPos l)
        {
            super(c, l);
        }

        @NotNull
        @Override
        public Window getWindow()
        {
            return new WindowHutChickenHerder(this);
        }
    }
}
