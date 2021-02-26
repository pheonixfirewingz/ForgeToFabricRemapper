package com.minecolonies.coremod.blocks.huts;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Hut for the Stash. No different from {@link AbstractBlockHut}
 */
public class BlockStash extends AbstractBlockHut<BlockStash> implements IRSComponentBlock
{

    private static final VoxelShape SHAPE_NORTH = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 8.0D);
    private static final VoxelShape SHAPE_EAST  = Block.makeCuboidShape(8.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape SHAPE_SOUTH = Block.makeCuboidShape(0.0D, 0.0D, 8.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape SHAPE_WEST  = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 8.0D, 16.0D, 16.0D);

    @NotNull
    @Override
    public String getName()
    {
        return "blockstash";
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
        final TileEntityColonyBuilding building = (TileEntityColonyBuilding) MinecoloniesTileEntities.STASH.create();
        building.registryName = this.getBuildingEntry().getRegistryName();
        return building;
    }

    @Override
    public BuildingEntry getBuildingEntry()
    {
        return ModBuildings.stash;
    }

    @NotNull
    @Override
    public VoxelShape getShape(
      final BlockState state, final IBlockReader worldIn, final BlockPos pos, final ISelectionContext context)
    {
        switch (state.get(FACING))
        {
            case NORTH:
                return SHAPE_NORTH;
            case SOUTH:
                return SHAPE_SOUTH;
            case EAST:
                return SHAPE_EAST;
            default:
                return SHAPE_WEST;
        }
    }

    @NotNull
    @Override
    public ActionResultType onBlockActivated(
      final BlockState state,
      final World worldIn,
      final BlockPos pos,
      final PlayerEntity player,
      final Hand hand,
      final BlockRayTraceResult ray)
    {
        if (worldIn.isRemote)
        {
            @Nullable final IBuildingView building = IColonyManager.getInstance().getBuildingView(worldIn.getDimensionKey(), pos);

            if (building != null
                  && building.getColony() != null
                  && building.getColony().getPermissions().hasPermission(player, Action.ACCESS_HUTS))
            {
                Network.getNetwork().sendToServer(new OpenInventoryMessage(building));
            }
        }
        return ActionResultType.SUCCESS;
    }
}
