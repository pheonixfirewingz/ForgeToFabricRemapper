package com.minecolonies.coremod.colony.colony.buildings.workerbuildings;

import org.jetbrains.annotations.NotNull;


/**
 * Creates a new building for the rabbit hutch.
 */
public class BuildingRabbitHutch extends AbstractBuildingHerder
{
    /**
     * Description of the job executed in the hut.
     */
    private static final String RABBIT_HUTCH = "rabbithutch";

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
    public BuildingRabbitHutch(final IColony c, final BlockPos l)
    {
        super(c, l);
        keepX.put(stack -> Items.CARROT == stack.getItem(), new Tuple<>(STACKSIZE, true));
    }

    @NotNull
    @Override
    public String getSchematicName()
    {
        return RABBIT_HUTCH;
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
        return RABBIT_HUTCH;
    }

    @NotNull
    @Override
    public Skill getPrimarySkill()
    {
        return Skill.Agility;
    }

    @NotNull
    @Override
    public Skill getSecondarySkill()
    {
        return Skill.Athletics;
    }

    /**
     * The abstract method which creates a job for the building.
     *
     * @param citizen the citizen to take the job.
     * @return the Job.
     */
    @NotNull
    @Override
    public IJob<?> createJob(final ICitizenData citizen)
    {
        return new JobRabbitHerder(citizen);
    }

    @Override
    public BuildingEntry getBuildingRegistryEntry()
    {
        return ModBuildings.rabbitHutch;
    }

    @Override
    public boolean canEat(final ItemStack stack)
    {
        if (stack.getItem() == Items.CARROT)
        {
            return false;
        }
        return super.canEat(stack);
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
            return new WindowHutWorkerPlaceholder<AbstractBuildingHerder.View>(this, RABBIT_HUTCH);
        }
    }
}
