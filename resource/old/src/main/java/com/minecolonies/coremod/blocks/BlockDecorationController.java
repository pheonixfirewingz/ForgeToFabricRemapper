package com.minecolonies.coremod.blocks;

import com.minecolonies.api.entity.ai.citizen.builder.IBuilderUndestroyable;
import com.minecolonies.coremod.tileentities.TileEntityDecorationController;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.property.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.*;
import net.minecraft.world.World;
/**
 * Creates a decoration placerholder block.
 */
public class BlockDecorationController extends Block implements IBuilderUndestroyable, IAnchorBlock
{
    /**
     * The hardness this block has.
     */
    private static final float BLOCK_HARDNESS = 5F;

    /**
     * The resistance this block has.
     */
    private static final float RESISTANCE = 1F;

    /**
     * If the block is mirrored.
     */
    public static BooleanProperty MIRROR = BooleanProperty.of("mirror");
    public static final DirectionProperty HORIZONTAL_FACING = HorizontalBlock.HORIZONTAL_FACING;

    /**
     * The bounding boxes.
     */
    protected static final VoxelShape AABB_SOUTH = VoxelShapes.cuboid(0.25D, 0.314D, 0.97D, 0.75D, 0.86D, 1.0D);
    protected static final VoxelShape AABB_NORTH = VoxelShapes.cuboid(0.25D, 0.314D, 0.0D, 0.75D, 0.86D, 0.3D);
    protected static final VoxelShape AABB_EAST  = VoxelShapes.cuboid(0.97D, 0.314D, 0.25D, 1.0D, 0.86D, 0.75D);
    protected static final VoxelShape AABB_WEST  = VoxelShapes.cuboid(0.0D, 0.314D, 0.25D, 0.3D, 0.86D, 0.75D);

    /**
     * Constructor for the placerholder.
     */
    public BlockDecorationController()
    {
        super(Settings.of(Material.WOOD).strength(BLOCK_HARDNESS, RESISTANCE).doesNotBlockMovement());
        this.setDefaultState(this.getDefaultState().with(HORIZONTAL_FACING, Direction.NORTH).with(MIRROR, false));
    }

    @Override
    public VoxelShape getShape(final BlockState state, final IBlockReader worldIn, final BlockPos pos, final ISelectionContext context)
    {
        Direction Direction = state.get(HORIZONTAL_FACING);
        switch (Direction)
        {
            case EAST:
                return AABB_EAST;
            case WEST:
                return AABB_WEST;
            case SOUTH:
                return AABB_SOUTH;
            case NORTH:
            default:
                return AABB_NORTH;
        }
    }

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
            final TileEntity tileEntity = worldIn.getTileEntity(pos);
            if (tileEntity instanceof TileEntityDecorationController)
            {
                MineColonies.proxy.openDecorationControllerWindow(pos);
            }
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public boolean hasTileEntity(final BlockState state)
    {
        return true;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(HORIZONTAL_FACING, MIRROR);
    }

    @Nullable
    @Override
    public TileEntity cuboidTileEntity(final BlockState state, final IBlockReader world)
    {
        return new TileEntityDecorationController();
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(final BlockItemUseContext context)
    {
        return super.getStateForPlacement(context).with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing());
    }
    /**
     * Convert the BlockState into the correct metadata value.
     *
     * @deprecated (Remove this as soon as minecraft offers anything better).
     */
    @Override public BlockState rotate(BlockState state, BlockRotation rotation)
    {
        return state.with(HORIZONTAL_FACING, rotation.rotate(state.get(HORIZONTAL_FACING)));
    }

    /**
     * @deprecated (Remove this as soon as minecraft offers anything better).
     */
    @Override public BlockState mirror(BlockState state, BlockMirror mirror)
    {
        return state.with(MIRROR, mirror != BlockMirror.NONE);
    }
    
}
