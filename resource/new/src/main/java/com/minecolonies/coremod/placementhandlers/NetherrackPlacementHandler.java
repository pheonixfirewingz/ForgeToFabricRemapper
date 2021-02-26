package com.minecolonies.coremod.placementhandlers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class NetherrackPlacementHandler implements IPlacementHandler
{
    @Override
    public boolean canHandle(@NotNull World world, @NotNull BlockPos pos, @NotNull BlockState blockState)
    {
        return blockState.getBlock() instanceof NyliumBlock;
    }

    @Override
    public ActionProcessingResult handle(
      @NotNull World world,
      @NotNull BlockPos pos,
      @NotNull BlockState blockState,
      @Nullable CompoundNBT tileEntityData,
      boolean complete,
      BlockPos centerPos)
    {
        return !world.setBlockState(pos, blockState, 3) ? ActionProcessingResult.DENY : ActionProcessingResult.SUCCESS;
    }

    @Override
    public List<ItemStack> getRequiredItems(@NotNull World world, @NotNull BlockPos pos, @NotNull BlockState blockState, @Nullable CompoundNBT tileEntityData, boolean complete)
    {
        List<ItemStack> itemList = new ArrayList<>();
        itemList.add(new ItemStack(Blocks.NETHERRACK));
        return itemList;
    }
}
