package com.minecolonies.coremod.colony.colony.managers;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


/**
 * The Progress manager which tracks the colony progress to send help messages to the player.
 */
{
    /**
     * The progress events which have been broadcasted to the colony already.
     */
    private final List<ColonyProgressType> notifiedProgress = new ArrayList<>();

    /**
     * The connected colony.
     */
    private final Colony colony;

    /**
     * If progress should be printed.
     */
    private boolean printProgress = true;

    /**
     * Creates the progress for a colony.
     *
     * @param colony the colony.
     */
    public ProgressManager(final Colony colony)
    {
        this.colony = colony;
    }

    
    public void progressBuildingPlacement(final Block block)
    {
        if (block == ModBlocks.blockHutTownHall)
        {
            trigger(COLONY_FOUNDED);
        }
        else if (block == ModBlocks.blockHutBuilder)
        {
            trigger(BUILDER_PLACED);
        }
    }

    
    public void progressCitizenSpawn(final int total, final int employed)
    {
        if (total == 1)
        {
            trigger(FIRST_CITIZEN_SPAWNED);
        }
        else if (total == 4)
        {
            trigger(FOUR_CITIZENS_SPAWNED);
        }
        else if (total == 6)
        {
            trigger(SIX_CITIZENS_SPAWNED);
        }
        else if (total == 7)
        {
            trigger(SEVEN_CITIZENS_SPAWNED);
        }
        else if (total == 8)
        {
            trigger(EIGHT_CITIZENS_SPAWNED);
        }
        else if (total == 9)
        {
            trigger(NINE_CITIZENS_SPAWNED);
        }
        else if (total == 10)
        {
            trigger(TEN_CITIZENS_SPAWNED);
        }
        else if (total == 25)
        {
            trigger(TWENTY_FIVE_CITIZENS_SPAWNED);
        }
        else if (total >= 11 && employed >= 4)
        {
            trigger(NOT_ENOUGH_JOBS);
        }
    }

    
    public void progressWorkOrderPlacement(final IWorkOrder workOrder)
    {
        if (workOrder instanceof WorkOrderBuildBuilding && ((WorkOrderBuildBuilding) workOrder).getStructureName().contains("Builder"))
        {
            trigger(BUILT_ENQUEUED);
        }
    }

    
    public void progressBuildBuilding(final IBuilding building, final int totalLevels, final int totalHousing)
    {
        if (building instanceof BuildingBuilder)
        {
            trigger(BUILDER_BUILT);
        }
        else if (building instanceof BuildingMiner || building instanceof BuildingLumberjack)
        {
            trigger(RESOURCE_PROD_BUILT);
        }
        else if (building instanceof BuildingFisherman || building instanceof BuildingFarmer)
        {
            trigger(FOOD_PROD_BUILT);
        }
        else if (totalHousing == 4 && (building instanceof BuildingTownHall || building.hasModule(LivingBuildingModule.class)))
        {
            trigger(ALL_CITIZENS_HOMED);
        }
        else if (building instanceof BuildingWareHouse)
        {
            trigger(WAREHOUSE_BUILT);
        }
        else if (totalLevels == 20)
        {
            trigger(TWENTY_BUILDING_LEVELS);
        }
    }

    
    public void progressEmploy(final int employed)
    {
        if (employed == 4)
        {
            trigger(FOUR_CITIZEN_EMPLOYED);
        }
    }

    
    public void progressEmploymentModeChange()
    {
        trigger(MANUAL_EMPLOYMENT_ON);
    }

    
    public void trigger(final ColonyProgressType type)
    {
        if (!printProgress)
        {
            return;
        }

        if (!notifiedProgress.contains(type))
        {
            notifiedProgress.add(type);
            LanguageHandler.sendPlayersMessage(colony.getMessagePlayerEntities(), "com.minecolonies.coremod.progress." + type.name().toLowerCase(Locale.US));
            colony.markDirty();
        }
    }

    
    public void togglePrintProgress()
    {
        printProgress = !printProgress;
        colony.markDirty();
    }

    
    public boolean isPrintingProgress()
    {
        return printProgress;
    }

    
    public void read(@NotNull final CompoundNBT compound)
    {
        notifiedProgress.clear();
        final CompoundNBT progressCompound = compound.getCompound(TAG_PROGRESS_MANAGER);
        final ListNBT progressTags = progressCompound.getList(TAG_PROGRESS_LIST, Constants.NBT.TAG_COMPOUND);
        notifiedProgress.addAll(NBTUtils.streamCompound(progressTags)
                                  .map(progressTypeCompound -> values()[progressTypeCompound.getInt(TAG_PROGRESS_TYPE)])
                                  .collect(Collectors.toList()));
        printProgress = progressCompound.getBoolean(TAG_PRINT_PROGRESS);
    }

    
    public void write(@NotNull final CompoundNBT compound)
    {
        final CompoundNBT progressCompound = new CompoundNBT();
        @NotNull final ListNBT progressTagList = notifiedProgress.stream()
                                                   .map(this::writeProgressTypeToNBT)
                                                   .collect(NBTUtils.toListNBT());

        progressCompound.put(TAG_PROGRESS_LIST, progressTagList);
        progressCompound.putBoolean(TAG_PRINT_PROGRESS, printProgress);
        compound.put(TAG_PROGRESS_MANAGER, progressCompound);
    }

    /**
     * Writes a single colony progress type to NBT.
     *
     * @param type the type.
     * @return the NBT representation.
     */
    private CompoundNBT writeProgressTypeToNBT(final ColonyProgressType type)
    {
        final CompoundNBT compound = new CompoundNBT();
        compound.putInt(TAG_PROGRESS_TYPE, type.ordinal());
        return compound;
    }
}
