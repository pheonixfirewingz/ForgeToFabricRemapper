package com.minecolonies.coremod.placementhandlers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FieldPlacementHandler implements IPlacementHandler
{
    @Override
    public boolean canHandle(@NotNull World world, @NotNull BlockPos pos, @NotNull BlockState blockState)
    {
        return blockState.getBlock() instanceof BlockScarecrow;
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
        if (world.getBlockState(pos).getBlock() == ModBlocks.blockScarecrow)
        {
            return ActionProcessingResult.SUCCESS;
        }

        if (blockState.get(DoorBlock.HALF).equals(DoubleBlockHalf.LOWER))
        {
            world.setBlockState(pos, blockState.with(DoorBlock.HALF, DoubleBlockHalf.LOWER), 3);
            world.setBlockState(pos.up(), blockState.with(DoorBlock.HALF, DoubleBlockHalf.UPPER), 3);
        }

        return ActionProcessingResult.SUCCESS;
    }

    @Override
    public List<ItemStack> getRequiredItems(@NotNull World world, @NotNull BlockPos pos, @NotNull BlockState blockState, @Nullable CompoundNBT tileEntityData, boolean complete)
    {
        List<ItemStack> itemList = new ArrayList<>();
        if (blockState.get(DoorBlock.HALF).equals(DoubleBlockHalf.LOWER))
        {
            itemList.add(BlockUtils.getItemStackFromBlockState(blockState));
        }

        return itemList;
    }
}
