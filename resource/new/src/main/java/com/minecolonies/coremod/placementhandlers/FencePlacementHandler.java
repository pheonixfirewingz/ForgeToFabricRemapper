package com.minecolonies.coremod.placementhandlers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


public class FencePlacementHandler implements IPlacementHandler
{
    @Override
    public boolean canHandle(@NotNull final World world, @NotNull final BlockPos pos, @NotNull final BlockState blockState)
    {
        return BlockTags.FENCES.contains(blockState.getBlock());
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

        BlockState northState = world.getBlockState(pos.north());
        BlockState eastState = world.getBlockState(pos.east());
        BlockState southState = world.getBlockState(pos.south());
        BlockState westState = world.getBlockState(pos.west());
        final BlockState fence = blockState
                                   .with(FenceBlock.NORTH,
                                     ((FenceBlock) blockState.getBlock()).canConnect(northState, northState.isSolidSide(world, pos.north(), Direction.SOUTH), Direction.SOUTH))
                                   .with(FenceBlock.EAST,
                                     ((FenceBlock) blockState.getBlock()).canConnect(eastState, eastState.isSolidSide(world, pos.east(), Direction.WEST), Direction.WEST))
                                   .with(FenceBlock.SOUTH,
                                     ((FenceBlock) blockState.getBlock()).canConnect(southState, southState.isSolidSide(world, pos.south(), Direction.NORTH), Direction.NORTH))
                                   .with(FenceBlock.WEST,
                                     ((FenceBlock) blockState.getBlock()).canConnect(westState, westState.isSolidSide(world, pos.west(), Direction.EAST), Direction.EAST));

        if (!world.setBlockState(pos, fence, UPDATE_FLAG))
        {
            return ActionProcessingResult.PASS;
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
        final List<ItemStack> itemList = new ArrayList<>();
        itemList.add(BlockUtils.getItemStackFromBlockState(blockState));
        itemList.removeIf(ItemStackUtils::isEmpty);
        return itemList;
    }
}
