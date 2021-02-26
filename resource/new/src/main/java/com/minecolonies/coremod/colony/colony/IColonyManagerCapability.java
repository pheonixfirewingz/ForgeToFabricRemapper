package com.minecolonies.coremod.colony.colony;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;


/**
 * Capability for the colony tag for chunks
 */
public interface ColonyManagerCapability
{
    /**
     * Create a colony and return it.
     *
     * @param w   the world the colony is in.
     * @param pos the position of the colony.
     * @return the created colony.
     */
    Colony createColony(@NotNull final World w, @NotNull final BlockPos pos);

    /**
     * Delete a colony with a certain id.
     *
     * @param id the id of the colony.
     */
    void deleteColony(final int id);

    /**
     * Get a colony with a certain id.
     *
     * @param id the id of the colony.
     * @return the colony or null.
     */
    @Nullable
    Colony getColony(final int id);

    /**
     * Get a list of all colonies.
     *
     * @return a complete list.
     */
    List<Colony> getColonies();

    /**
     * add a new colony to the capability.
     *
     * @param colony the colony to add.
     */
    void addColony(Colony colony);

    /**
     * Get the top most id of all colonies.
     *
     * @return the top most id.
     */
    int getTopID();

    /**
     * The implementation of the colonyTagCapability.
     */
    class Impl implements ColonyManagerCapability
    {
        /**
         * The list of all colonies.
         */
        @NotNull
        private final ColonyList<Colony> colonies = new ColonyList<>();

        @Override
        public Colony createColony(@NotNull final World w, @NotNull final BlockPos pos)
        {
            return colonies.create(w, pos);
        }

        @Override
        public void deleteColony(final int id)
        {
            colonies.remove(id);
        }

        @Override
        public Colony getColony(final int id)
        {
            return colonies.get(id);
        }

        @Override
        public List<Colony> getColonies()
        {
            return colonies.getCopyAsList();
        }

        @Override
        public void addColony(final Colony colony)
        {
            colonies.add(colony);
        }

        @Override
        public int getTopID()
        {
            return colonies.getTopID();
        }
    }

    /**
     * The storage class of the capability.
     */
    class Storage implements Capability.IStorage<ColonyManagerCapability>
    {

        @Override
        public INBT writeNBT(@NotNull final Capability<ColonyManagerCapability> capability, @NotNull final ColonyManagerCapability instance, @Nullable final Direction side)
        {
            final CompoundNBT compound = new CompoundNBT();
            compound.put(TAG_COLONIES, instance.getColonies().stream().map(Colony::getColonyTag).filter(Objects::nonNull).collect(NBTUtils.toListNBT()));
            final CompoundNBT managerCompound = new CompoundNBT();
            ColonyManager.getInstance().write(managerCompound);
            compound.put(TAG_COLONY_MANAGER, managerCompound);
            return compound;
        }

        @Override
        public void readNBT(
          @NotNull final Capability<ColonyManagerCapability> capability, @NotNull final ColonyManagerCapability instance,
          @Nullable final Direction side, @NotNull final INBT nbt)
        {
            if (nbt instanceof CompoundNBT)
            {
                final CompoundNBT compound = (CompoundNBT) nbt;

                // Load all colonies from Nbt
                Multimap<BlockPos, Colony> tempColonies = ArrayListMultimap.create();
                for (final INBT tag : compound.getList(TAG_COLONIES, Constants.NBT.TAG_COMPOUND))
                {
                    final Colony colony = Colony.loadColony((CompoundNBT) tag, null);
                    if (colony != null)
                    {
                        tempColonies.put(colony.getCenter(), colony);
                        instance.addColony(colony);
                    }
                }

                // Check colonies for duplicates causing issues.
                for (final BlockPos pos : tempColonies.keySet())
                {
                    // Check if any position has more than one colony
                    if (tempColonies.get(pos).size() > 1)
                    {
                        Log.getLogger().warn("Detected duplicate colonies which are at the same position:");
                        for (final Colony colony : tempColonies.get(pos))
                        {
                            Log.getLogger()
                              .warn(
                                "ID: " + colony.getID() + " name:" + colony.getName() + " citizens:" + colony.getCitizenManager().getCitizens().size() + " building count:" + colony
                                                                                                                                                                                .getBuildingManager()
                                                                                                                                                                                .getBuildings()
                                                                                                                                                                                .size());
                        }
                        Log.getLogger().warn("Check and remove all except one of the duplicated colonies above!");
                    }
                }

                if (compound.keySet().contains(TAG_COLONY_MANAGER))
                {
                    ColonyManager.getInstance().read(compound.getCompound(TAG_COLONY_MANAGER));
                }
            }
        }
    }
}
