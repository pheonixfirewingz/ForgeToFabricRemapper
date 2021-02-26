package com.minecolonies.coremod.placementhandlers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;


public class BuildingBarracksTowerSub implements IPlacementHandler
{
    @Override
    public boolean canHandle(@NotNull final World world, @NotNull final BlockPos pos, @NotNull final BlockState blockState)
    {
        return blockState.getBlock() == ModBlocks.blockBarracksTowerSubstitution;
    }

    @Override
    public ActionProcessingResult handle(
      @NotNull final World world,
      @NotNull final BlockPos pos,
      @NotNull final BlockState blockState,
      @Nullable final CompoundNBT tileEntityData,
      final boolean complete,
      final BlockPos centerPos,
      final PlacementSettings settings)
    {
        if (world.getBlockState(pos).equals(blockState))
        {
            return ActionProcessingResult.PASS;
        }

        if (!world.setBlockState(pos, blockState, com.ldtteam.structurize.api.util.constant.Constants.UPDATE_FLAG))
        {
            return ActionProcessingResult.PASS;
        }

        if (tileEntityData != null)
        {
            try
            {
                handleTileEntityPlacement(tileEntityData, world, pos, settings);
            }
            catch (final Exception ex)
            {
                Log.getLogger().warn("Unable to place TileEntity");
            }
        }

        return ActionProcessingResult.SUCCESS;
    }

    @Override
    public List<ItemStack> getRequiredItems(
      @NotNull final World world,
      @NotNull final BlockPos pos,
      @NotNull final BlockState blockState,
      @Nullable final CompoundNBT tileEntityData,
      final boolean complete)
    {
        return Collections.emptyList();
    }
}
