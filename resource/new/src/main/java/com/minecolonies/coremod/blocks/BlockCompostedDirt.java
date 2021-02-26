package com.minecolonies.coremod.blocks;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * Block that if activated with BoneMeal or Compost by an AI will produce flowers by intervals until it deactivates
 */
public class BlockCompostedDirt extends Block
{
    private static final String     BLOCK_NAME     = "composted_dirt";
    private static final float      BLOCK_HARDNESS = 5f;
    private static final float      RESISTANCE     = 1f;
    private final static VoxelShape SHAPE          = VoxelShapes.cuboid(0, 0, 0, 1, 1, 1);

    /**
     * The constructor of the block.
     */
    public BlockCompostedDirt()
    {
        super(Settings.of(Material.GOURD).strength(BLOCK_HARDNESS, RESISTANCE));
    }

    @Override
    public boolean hasTileEntity(final BlockState state)
    {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(final BlockState state, final IBlockReader world)
    {
        return new TileEntityCompostedDirt();
    }

    @NotNull
    @Override
    public VoxelShape getShape(final BlockState state, final IBlockReader worldIn, final BlockPos pos, final ISelectionContext context)
    {
        return SHAPE;
    }

    @Override
    public boolean canSustainPlant(
      @NotNull final BlockState state,
      @NotNull final IBlockReader world,
      final BlockPos pos,
      @NotNull final Direction facing,
      final IPlantable plantable)
    {
        return true;
    }
}
