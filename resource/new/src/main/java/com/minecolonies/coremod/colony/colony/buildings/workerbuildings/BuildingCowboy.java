package com.minecolonies.coremod.colony.colony.buildings.workerbuildings;

import org.jetbrains.annotations.NotNull;


/**
 * Creates a new building for the Cowboy.
 */
public class BuildingCowboy extends AbstractBuildingHerder
{
    /**
     * Description of the job executed in the hut.
     */
    private static final String COWBOY = "cowboy";

    /**
     * Max building level of the hut.
     */
    private static final int MAX_BUILDING_LEVEL = 5;

    /**
     * Milk Cows or not.
     */
    private boolean milkCows = true;

    /**
     * Instantiates the building.
     *
     * @param c the colony.
     * @param l the location.
     */
    public BuildingCowboy(final IColony c, final BlockPos l)
    {
        super(c, l);
    }

    @NotNull
    @Override
    public String getSchematicName()
    {
        return COWBOY;
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
        return COWBOY;
    }

    @NotNull
    @Override
    public Skill getPrimarySkill()
    {
        return Skill.Athletics;
    }

    @NotNull
    @Override
    public Skill getSecondarySkill()
    {
        return Skill.Stamina;
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
        return new JobCowboy(citizen);
    }

    @Override
    public void serializeToView(@NotNull final PacketBuffer buf)
    {
        super.serializeToView(buf);
        buf.writeBoolean(milkCows);
    }

    @Override
    public BuildingEntry getBuildingRegistryEntry()
    {
        return ModBuildings.cowboy;
    }

    @Override
    public void deserializeNBT(final CompoundNBT compound)
    {
        super.deserializeNBT(compound);
        this.milkCows = compound.getBoolean(TAG_MILK_COWS);
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        final CompoundNBT compound = super.serializeNBT();
        compound.putBoolean(TAG_MILK_COWS, this.milkCows);
        return compound;
    }

    public boolean isMilkingCows()
    {
        return milkCows;
    }

    public void setMilkCows(final boolean milkCows)
    {
        this.milkCows = milkCows;
        markDirty();
    }

    @Override
    public boolean canEat(final ItemStack stack)
    {
        if (stack.getItem() == Items.WHEAT)
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
         * Milk Cows or not.
         */
        private boolean milkCows = true;

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
            return new WindowHutCowboy(this);
        }

        public void setMilkCows(final boolean milkCows)
        {
            Network.getNetwork().sendToServer(new CowboySetMilkCowsMessage(this, milkCows));
            this.milkCows = milkCows;
        }

        public boolean isMilkCows()
        {
            return milkCows;
        }

        @Override
        public void deserialize(@NotNull final PacketBuffer buf)
        {
            super.deserialize(buf);
            milkCows = buf.readBoolean();
        }
    }
}
