package com.minecolonies.coremod.colony.colony.buildings.workerbuildings;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;


/**
 * Class of the hospital building.
 */
@SuppressWarnings(OVERRIDE_EQUALS)
public class BuildingHospital extends AbstractBuildingFurnaceUser
{
    /**
     * The hospital string.
     */
    private static final String HOSPITAL_DESC = "hospital";

    /**
     * Max building level of the hospital.
     */
    private static final int MAX_BUILDING_LEVEL = 5;

    /**
     * Map from beds to patients, 0 is empty.
     */
    @NotNull
    private final Map<BlockPos, Integer> bedMap = new HashMap<>();

    /**
     * Map of patients of this hospital.
     */
    private final Map<Integer, Patient> patients = new HashMap<>();

    /**
     * Instantiates a new hospital building.
     *
     * @param c the colony.
     * @param l the location
     */
    public BuildingHospital(final IColony c, final BlockPos l)
    {
        super(c, l);
    }

    @NotNull
    @Override
    public String getSchematicName()
    {
        return HOSPITAL_DESC;
    }

    @Override
    public int getMaxBuildingLevel()
    {
        return MAX_BUILDING_LEVEL;
    }

    @NotNull
    @Override
    public IJob<?> createJob(final ICitizenData citizen)
    {
        return new JobHealer(citizen);
    }

    @NotNull
    @Override
    public String getJobName()
    {
        return "com.minecolonies.coremod.job.healer";
    }

    @NotNull
    @Override
    public Skill getPrimarySkill()
    {
        return Skill.Mana;
    }

    @NotNull
    @Override
    public Skill getSecondarySkill()
    {
        return Skill.Knowledge;
    }

    @Override
    public boolean canWorkDuringTheRain()
    {
        return true;
    }

    @Override
    public BuildingEntry getBuildingRegistryEntry()
    {
        return ModBuildings.hospital;
    }

    @Override
    public void deserializeNBT(final CompoundNBT compound)
    {
        super.deserializeNBT(compound);
        final ListNBT bedTagList = compound.getList(TAG_BEDS, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < bedTagList.size(); ++i)
        {
            final CompoundNBT bedCompound = bedTagList.getCompound(i);
            final BlockPos bedPos = BlockPosUtil.read(bedCompound, TAG_POS);
            if (!bedMap.containsKey(bedPos))
            {
                bedMap.put(bedPos, bedCompound.getInt(TAG_ID));
            }
        }

        final ListNBT patientTagList = compound.getList(TAG_PATIENTS, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < patientTagList.size(); ++i)
        {
            final CompoundNBT patientCompound = patientTagList.getCompound(i);
            final int patientId = patientCompound.getInt(TAG_ID);
            if (!patients.containsKey(patientId))
            {
                patients.put(patientId, new Patient(patientCompound));
            }
        }
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        final CompoundNBT compound = super.serializeNBT();
        if (!bedMap.isEmpty())
        {
            @NotNull final ListNBT bedTagList = new ListNBT();
            for (@NotNull final Map.Entry<BlockPos, Integer> entry : bedMap.entrySet())
            {
                final CompoundNBT bedCompound = new CompoundNBT();
                BlockPosUtil.write(bedCompound, NbtTagConstants.TAG_POS, entry.getKey());
                bedCompound.putInt(TAG_ID, entry.getValue());
                bedTagList.add(bedCompound);
            }
            compound.put(TAG_BEDS, bedTagList);
        }

        if (!patients.isEmpty())
        {
            @NotNull final ListNBT patientTagList = new ListNBT();
            for (@NotNull final Patient patient : patients.values())
            {
                final CompoundNBT patientCompound = new CompoundNBT();
                patient.write(patientCompound);
                patientTagList.add(patientCompound);
            }
            compound.put(TAG_PATIENTS, patientTagList);
        }

        return compound;
    }

    @Override
    public void registerBlockPosition(@NotNull final BlockState blockState, @NotNull final BlockPos pos, @NotNull final World world)
    {
        super.registerBlockPosition(blockState, pos, world);

        BlockPos registrationPosition = pos;
        if (blockState.getBlock() instanceof BedBlock)
        {
            if (blockState.get(BedBlock.PART) == BedPart.FOOT)
            {
                registrationPosition = registrationPosition.offset(blockState.get(BedBlock.HORIZONTAL_FACING));
            }

            if (!bedMap.containsKey(registrationPosition))
            {
                bedMap.put(registrationPosition, 0);
            }
        }
    }

    @Override
    public void removeCitizen(final ICitizenData citizen)
    {
        if (citizen != null)
        {
            final Optional<AbstractEntityCitizen> optCitizen = citizen.getEntity();
            optCitizen.ifPresent(entityCitizen -> AttributeModifierUtils.removeModifier(entityCitizen, SKILL_BONUS_ADD, Attributes.MOVEMENT_SPEED));
        }
        super.removeCitizen(citizen);
    }

    /**
     * Get the list of beds.
     *
     * @return immutable copy
     */
    @NotNull
    public List<BlockPos> getBedList()
    {
        return ImmutableList.copyOf(bedMap.keySet());
    }

    /**
     * Get the list of patient files.
     *
     * @return immutable copy.
     */
    public List<Patient> getPatients()
    {
        return ImmutableList.copyOf(patients.values());
    }

    /**
     * Remove a patient from the list.
     *
     * @param patient the patient to remove.
     */
    public void removePatientFile(final Patient patient)
    {
        patients.remove(patient.getId());
    }

    @Override
    public Map<Predicate<ItemStack>, Tuple<Integer, Boolean>> getRequiredItemsAndAmount()
    {
        final Map<Predicate<ItemStack>, Tuple<Integer, Boolean>> map = super.getRequiredItemsAndAmount();
        map.put(this::doesAnyPatientRequireStack, new Tuple<>(10, false));
        return map;
    }

    /**
     * Check if any patient requires this.
     *
     * @param stack the stack to test.
     * @return true if so.
     */
    private boolean doesAnyPatientRequireStack(final ItemStack stack)
    {
        for (final Patient patient : patients.values())
        {
            final ICitizenData data = colony.getCitizenManager().getCivilian(patient.getId());
            if (data != null && data.getEntity().isPresent() && data.getEntity().get().getCitizenDiseaseHandler().isSick())
            {
                final String diseaseName = data.getEntity().get().getCitizenDiseaseHandler().getDisease();
                if (!diseaseName.isEmpty())
                {
                    final Disease disease = IColonyManager.getInstance().getCompatibilityManager().getDisease(diseaseName);
                    for (final ItemStack cure : disease.getCure())
                    {
                        if (cure.isItemEqual(stack))
                        {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Add a new patient to the list.
     *
     * @param citizenId patient to add.
     */
    public void checkOrCreatePatientFile(final int citizenId)
    {
        if (!patients.containsKey(citizenId))
        {
            patients.put(citizenId, new Patient(citizenId));
        }
    }

    /**
     * Register a citizen.
     *
     * @param bedPos    the pos.
     * @param citizenId the citizen id.
     */
    public void registerPatient(final BlockPos bedPos, final int citizenId)
    {
        bedMap.put(bedPos, citizenId);
        setBedOccupation(bedPos, citizenId != 0);
    }

    /**
     * Helper method to set bed occupation.
     *
     * @param bedPos   the position of the bed.
     * @param occupied if occupied.
     */
    private void setBedOccupation(final BlockPos bedPos, final boolean occupied)
    {
        final BlockState state = colony.getWorld().getBlockState(bedPos);
        if (state.getBlock().isIn(BlockTags.BEDS))
        {
            colony.getWorld().setBlockState(bedPos, state.with(BedBlock.OCCUPIED, occupied), 0x03);

            final BlockPos feetPos = bedPos.offset(state.get(BedBlock.HORIZONTAL_FACING).getOpposite());
            final BlockState feetState = colony.getWorld().getBlockState(feetPos);

            if (feetState.getBlock().isIn(BlockTags.BEDS))
            {
                colony.getWorld().setBlockState(feetPos, feetState.with(BedBlock.OCCUPIED, occupied), 0x03);
            }
        }
    }

    @Override
    public void onWakeUp()
    {
        for (final Map.Entry<BlockPos, Integer> entry : new ArrayList<>(bedMap.entrySet()))
        {
            final BlockState state = colony.getWorld().getBlockState(entry.getKey());
            if (state.getBlock() instanceof BedBlock)
            {
                if (entry.getValue() == 0 && state.get(BedBlock.OCCUPIED))
                {
                    setBedOccupation(entry.getKey(), false);
                }
                else if (entry.getValue() != 0)
                {
                    final ICitizenData citizen = colony.getCitizenManager().getCivilian(entry.getValue());
                    if (citizen != null)
                    {
                        if (state.get(BedBlock.OCCUPIED))
                        {
                            if (!citizen.isAsleep() || !citizen.getEntity().isPresent()
                                  || citizen.getEntity().get().getPosition().distanceSq(entry.getKey()) > 2.0)
                            {
                                setBedOccupation(entry.getKey(), false);
                                bedMap.put(entry.getKey(), 0);
                            }
                        }
                        else
                        {
                            if (citizen.isAsleep() && citizen.getEntity().isPresent() && citizen.getEntity().get().getPosition().distanceSq(entry.getKey()) < 2.0)
                            {
                                setBedOccupation(entry.getKey(), true);
                            }
                        }
                    }
                    else
                    {
                        bedMap.put(entry.getKey(), 0);
                    }
                }
            }
            else
            {
                bedMap.remove(entry.getKey());
            }
        }
    }

    @Override
    public boolean canEat(final ItemStack stack)
    {
        for (final Disease disease : IColonyManager.getInstance().getCompatibilityManager().getDiseases())
        {
            for (final ItemStack cure : disease.getCure())
            {
                if (cure.isItemEqual(stack))
                {
                    return false;
                }
            }
        }

        return super.canEat(stack);
    }

    @Override
    public void requestUpgrade(final PlayerEntity player, final BlockPos builder)
    {
        final UnlockBuildingResearchEffect effect = colony.getResearchManager().getResearchEffects().getEffect(ResearchInitializer.HOSPITAL_RESEARCH, UnlockBuildingResearchEffect.class);
        if (effect == null)
        {
            player.sendMessage(new TranslationTextComponent("com.minecolonies.coremod.research.havetounlock"), player.getUniqueID());
            return;
        }
        super.requestUpgrade(player, builder);
    }

    /**
     * BuildingHospital View.
     */
    public static class View extends AbstractBuildingWorker.View
    {
        /**
         * Instantiate the hospital view.
         *
         * @param c the colonyview to put it in
         * @param l the positon
         */
        public View(final IColonyView c, final BlockPos l)
        {
            super(c, l);
        }

        @NotNull
        @Override
        public Window getWindow()
        {
            return new WindowHutWorkerPlaceholder<>(this, HOSPITAL_DESC);
        }
    }
}
