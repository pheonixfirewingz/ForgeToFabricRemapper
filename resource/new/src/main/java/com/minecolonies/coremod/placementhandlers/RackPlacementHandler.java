package com.minecolonies.coremod.placementhandlers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


public class RackPlacementHandler implements IPlacementHandler
{
    @Override
    public boolean canHandle(@NotNull final World world, @NotNull final BlockPos pos, @NotNull final BlockState blockState)
    {
        return blockState.getBlock() instanceof BlockMinecoloniesRack;
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
        if (world.getBlockState(pos).getBlock() == ModBlocks.blockRack)
        {
            return ActionProcessingResult.SUCCESS;
        }

        TileEntity entity = world.getTileEntity(pos);
        if (entity instanceof ChestTileEntity)
        {
            BuildingWareHouse.handleBuildingOverChest(pos, (ChestTileEntity) entity, world, tileEntityData);
        }
        else
        {
            world.setBlockState(pos, blockState, UPDATE_FLAG);
            if (tileEntityData != null)
            {
                handleTileEntityPlacement(tileEntityData, world, pos, settings);
            }

            entity = world.getTileEntity(pos);
            if (entity instanceof TileEntityRack)
            {
                ((TileEntityRack) entity).updateBlockState();
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

        for (final ItemStack stack : PlacementHandlers.getItemsFromTileEntity(tileEntityData, world, pos))
        {
            if (!ItemStackUtils.isEmpty(stack))
            {
                itemList.add(stack);
            }
        }
        return itemList;
    }
}
