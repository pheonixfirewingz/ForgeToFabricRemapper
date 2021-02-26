package com.minecolonies.coremod.colony.colony.buildings.workerbuildings;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class BuildingBeekeeper extends AbstractBuildingWorker
{
    /**
     * Description of the job executed in the hut.
     */
    private static final String BEEKEEPER = "beekeeper";

    /**
     * List of hives.
     */
    private Set<BlockPos> hives = new HashSet<>();

    /**
     * Whether the beekeeper should harvest honeycombs or honey bottles
     */
    private boolean harvestHoneycombs = true;

    /**
     * The abstract constructor of the building.
     *
     * @param c the colony
     * @param l the position
     */
    public BuildingBeekeeper(@NotNull final IColony c, final BlockPos l)
    {
        super(c, l);
        keepX.put(stack -> Items.SHEARS == stack.getItem(), new Tuple<>(1, true));
        keepX.put(stack -> Items.GLASS_BOTTLE == stack.getItem(), new Tuple<>(4, true));
    }

    /**
     * The abstract method which creates a job for the building.
     *
     * @param citizen the citizen to take the job.
     * @return the Job.
     */
    @NotNull
    @Override
    public IJob createJob(final ICitizenData citizen)
    {
        return new JobBeekeeper(citizen);
    }

    /**
     * The abstract method which returns the name of the job.
     *
     * @return the job name.
     */
    @NotNull
    @Override
    public String getJobName()
    {
        return BEEKEEPER;
    }

    /**
     * Primary skill getter.
     *
     * @return the primary skill.
     */
    @NotNull
    @Override
    public Skill getPrimarySkill()
    {
        return Skill.Dexterity;
    }

    /**
     * Secondary skill getter.
     *
     * @return the secondary skill.
     */
    @NotNull
    @Override
    public Skill getSecondarySkill()
    {
        return Skill.Adaptability;
    }

    @Override
    public BuildingEntry getBuildingRegistryEntry()
    {
        return ModBuildings.beekeeper;
    }

    /**
     * Children must return the name of their structure.
     *
     * @return StructureProxy name.
     */
    @NotNull
    @Override
    public String getSchematicName()
    {
        return BEEKEEPER;
    }

    /**
     * Children must return their max building level.
     *
     * @return Max building level.
     */
    @Override
    public int getMaxBuildingLevel()
    {
        return CONST_DEFAULT_MAX_BUILDING_LEVEL;
    }

    @Override
    public void deserializeNBT(final CompoundNBT compound)
    {
        super.deserializeNBT(compound);
        NBTUtils.streamCompound(compound.getList(NbtTagConstants.TAG_HIVES, Constants.NBT.TAG_COMPOUND))
          .map(NBTUtil::readBlockPos)
          .forEach(this.hives::add);
        this.harvestHoneycombs = compound.getBoolean(NbtTagConstants.TAG_HARVEST_HONEYCOMBS);
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        final CompoundNBT nbt = super.serializeNBT();
        nbt.put(NbtTagConstants.TAG_HIVES, this.hives.stream().map(NBTUtil::writeBlockPos).collect(NBTUtils.toListNBT()));
        nbt.putBoolean(NbtTagConstants.TAG_HARVEST_HONEYCOMBS, this.harvestHoneycombs);
        return nbt;
    }

    @Override
    public void serializeToView(@NotNull final PacketBuffer buf)
    {
        super.serializeToView(buf);
        buf.writeBoolean(harvestHoneycombs);
    }

    /**
     * Get the hives/nests positions that belong to this beekeper
     *
     * @return te set of positions of hives/nests that belong to this beekeeper
     */
    public Set<BlockPos> getHives()
    {
        return Collections.unmodifiableSet(new HashSet<>(hives));
    }

    /**
     * Remove a hive/nest position from this beekeper
     *
     * @param pos the position to remove
     */
    public void removeHive(final BlockPos pos)
    {
        hives.remove(pos);
    }

    /**
     * Add a hive/nest position to this beekeper
     *
     * @param pos the position to add
     */
    public void addHive(final BlockPos pos)
    {
        hives.add(pos);
    }

    /**
     * Set the hives/nests positions that belong to this beekeeper
     *
     * @param hives the new set of psitions of hives/nests that belong to this beekeeper
     */
    public void setHives(final Set<BlockPos> hives)
    {
        this.hives = hives;
    }

    /**
     * Set wether the beekeeper should harvest honeycombs
     *
     * @param harvestHoneycombs if true the beekeeper will harvest honeycombs if false will harvest honeybottles
     */
    public void setHarvestHoneycombs(final boolean harvestHoneycombs)
    {
        this.harvestHoneycombs = harvestHoneycombs;
    }

    /**
     * Get wether the beekeeper should harvest honeycombs
     *
     * @return if true the beekeeper will harvest honeycombs if false will harvest honeybottles
     */
    public boolean shouldHarvestHoneycombs()
    {
        return harvestHoneycombs;
    }

    /**
     * Get the maximum amount of hives that can belong to this beekeeper
     *
     * @return the number of maximum hives that can belong to this beekeeper
     */
    public int getMaximumHives()
    {
        return (int) Math.pow(2, getBuildingLevel() - 1);
    }

    public static class View extends AbstractBuildingWorker.View
    {
        /**
         * Harvest honeycombs or not.
         */
        private boolean harvestHoneycombs = true;

        /**
         * Creates a building view.
         *
         * @param c ColonyView the building is in.
         * @param l The location of the building.
         */
        public View(final IColonyView c, @NotNull final BlockPos l)
        {
            super(c, l);
        }

        @Nullable
        @Override
        public Window getWindow()
        {
            return new WindowHutBeekeeper(this);
        }

        public boolean isHarvestHoneycombs()
        {
            return harvestHoneycombs;
        }

        public void setHarvestHoneycombs(final boolean harvestHoneycombs)
        {
            Network.getNetwork().sendToServer(new BeekeeperSetHarvestHoneycombsMessage(this, harvestHoneycombs));
            this.harvestHoneycombs = harvestHoneycombs;
        }

        @Override
        public void deserialize(@NotNull final PacketBuffer buf)
        {
            super.deserialize(buf);
            harvestHoneycombs = buf.readBoolean();
        }
    }
}
