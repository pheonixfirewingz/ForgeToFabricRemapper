package com.minecolonies.coremod.colony.colony.buildings.modules;

import org.jetbrains.annotations.NotNull;

import java.util.*;


/**
 * The class of the citizen hut.
 */
public class BedHandlingModule extends AbstractBuildingModule implements IModuleWithExternalBlocks, IPersistentModule, IBuildingEventsModule
{
    /**
     * List of all beds.
     */
    @NotNull
    private final Set<BlockPos> bedList = new HashSet<>();

    @Override
    public void deserializeNBT(final CompoundNBT compound)
    {
        final ListNBT bedTagList = compound.getList(TAG_BEDS, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < bedTagList.size(); ++i)
        {
            final CompoundNBT bedCompound = bedTagList.getCompound(i);
            final BlockPos bedPos = NBTUtil.readBlockPos(bedCompound);
            bedList.add(bedPos);
        }
    }

    @Override
    public void serializeNBT(final CompoundNBT compound)
    {
        if (!bedList.isEmpty())
        {
            @NotNull final ListNBT bedTagList = new ListNBT();
            for (@NotNull final BlockPos pos : bedList)
            {
                bedTagList.add(NBTUtil.writeBlockPos(pos));
            }
            compound.put(TAG_BEDS, bedTagList);
        }
    }

    @Override
    public void onBlockPlacedInBuilding(@NotNull final BlockState blockState, @NotNull final BlockPos pos, @NotNull final World world)
    {
        BlockPos registrationPosition = pos;
        if (blockState.getBlock() instanceof BedBlock)
        {
            if (blockState.get(BedBlock.PART) == BedPart.FOOT)
            {
                registrationPosition = registrationPosition.offset(blockState.get(BedBlock.HORIZONTAL_FACING));
            }

            bedList.add(registrationPosition);
        }
    }

    @Override
    public List<BlockPos> getRegisteredBlocks()
    {
        return new ArrayList<>(bedList);
    }

    @Override
    public void onWakeUp()
    {
        final World world = building.getColony().getWorld();
        if (world == null)
        {
            return;
        }

        for (final BlockPos pos : bedList)
        {
            final BlockState state = world.getBlockState(pos);
            if (state.getBlock() instanceof BedBlock
                  && state.get(BedBlock.OCCUPIED)
                  && state.get(BedBlock.PART).equals(BedPart.HEAD))
            {
                world.setBlockState(pos, state.with(BedBlock.OCCUPIED, false), 0x03);
            }
        }
    }

}
