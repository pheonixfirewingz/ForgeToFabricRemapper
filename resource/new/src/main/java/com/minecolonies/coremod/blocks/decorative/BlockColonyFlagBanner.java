package com.minecolonies.coremod.blocks.decorative;


import java.util.Map;

/**
 * A custom banner block to construct the associated tile entity that will render the colony flag.
 * This is the floor version. For the wall version: {@link BlockColonyFlagWallBanner}
 */
public class BlockColonyFlagBanner extends AbstractBannerBlock implements BlockEntityProvider
{
    public static final IntProperty ROTATION = BlockStateProperties.ROTATION_0_15;
    private static final VoxelShape SHAPE = VoxelShape.cuboid(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);


    public BlockColonyFlagBanner()
    {
        super(DyeColor.WHITE,
                Settings.of(Material.WOOD).strength(1F).sounds()
                        .doesNotBlockMovement()
                        .sound(SoundType.WOOD));

        this.setDefaultState(this.stateContainer.getBaseState().with(ROTATION, Integer.valueOf(0)));
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        return worldIn.getBlockState(pos.down()).getMaterial().isSolid();
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return SHAPE;
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return this.getDefaultState()
                .with(ROTATION, Integer.valueOf(MathHelper.floor((double)((180.0F + context.getPlacementYaw()) * 16.0F / 360.0F) + 0.5D) & 15));
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        return facing == Direction.DOWN && !stateIn.isValidPosition(worldIn, currentPos)
                ? Blocks.AIR.getDefaultState()
                : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot)
    {
        return state.with(ROTATION, Integer.valueOf(rot.rotate(state.get(ROTATION), 16)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn)
    {
        return state.with(ROTATION, Integer.valueOf(mirrorIn.mirrorRotation(state.get(ROTATION), 16)));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(ROTATION);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        if(worldIn.isClient()) return;

        TileEntity te = worldIn.getTileEntity(pos);
        if(te instanceof TileEntityColonyFlag)
        {
            IColony colony = IColonyManager.getInstance().getIColony(worldIn, pos);

            // Allow the player to place their own beyond the colony
            if(colony == null && placer instanceof PlayerEntity)
                IColonyManager.getInstance().getIColonyByOwner(worldIn, (PlayerEntity) placer);

            if(colony != null)
                ((TileEntityColonyFlag) te).colonyId = colony.getID();
        }

    }

    @Override
    public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity instanceof TileEntityColonyFlag ? ((TileEntityColonyFlag) tileentity).getItem() : super.getItem(worldIn, pos, state);
    }
}
