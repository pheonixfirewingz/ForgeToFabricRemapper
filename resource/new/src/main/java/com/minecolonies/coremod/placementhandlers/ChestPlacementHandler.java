package com.minecolonies.coremod.placementhandlers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


public class ChestPlacementHandler implements IPlacementHandler
{
    @Override
    public boolean canHandle(@NotNull final World world, @NotNull final BlockPos pos, @NotNull final BlockState blockState)
    {
        return blockState.getBlock() instanceof AbstractChestBlock;
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
        final TileEntity entity = world.getTileEntity(pos);
        final IColony colony = IColonyManager.getInstance().getClosestColony(world, pos);
        if (colony != null && entity instanceof ChestTileEntity)
        {
            BuildingWareHouse.handleBuildingOverChest(pos, (ChestTileEntity) entity, world, tileEntityData);
        }
        else
        {
            if (!world.setBlockState(pos, blockState, UPDATE_FLAG))
            {
                return ActionProcessingResult.DENY;
            }

            if (tileEntityData != null)
            {
                handleTileEntityPlacement(tileEntityData, world, pos, settings);
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
        final List<ItemStack> itemList = new ArrayList<>();
        itemList.add(BlockUtils.getItemStackFromBlockState(blockState));
        itemList.addAll(ItemStackUtils.getItemStacksOfTileEntity(tileEntityData, world, pos));

        itemList.removeIf(ItemStackUtils::isEmpty);

        return itemList;
    }
}
