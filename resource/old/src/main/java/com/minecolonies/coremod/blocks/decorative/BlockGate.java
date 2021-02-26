package com.minecolonies.coremod.blocks.decorative;

import com.minecolonies.api.util.WorldUtil;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.state.*;
import net.minecraft.state.property.*;
import net.minecraft.util.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.*;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.*;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;

import static net.minecraft.state.property.Properties.*;


/**
 * Gate blocks used for defending the colony against raiders
 */
public class BlockGate extends DoorBlock
{
    /**
     * Max gate size
     */
    private final int maxWidth = 7;
    private final int maxHeight = 9;

    private final float hardness;
    /**
     * The bounding boxes.
     */
    protected static final VoxelShape E_W_SHAPE = VoxelShapes.cuboid(0.3D, 0.0D, 0.0D, 0.7D, 1.0D, 1.0D);
    protected static final VoxelShape N_S_SHAPE = VoxelShapes.cuboid(0.0D, 0.0D, 0.3D, 1.0D, 1.0D, 0.7D);

    public BlockGate(final float hardness)
    {
        super(Settings.of(Material.WOOD).strength(hardness, hardness * 5).noCollision());
        setDefaultState(getDefaultState());

        this.hardness = hardness;
    }

    @Override public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (this.material == Material.METAL)
            return ActionResult.PASS;
        else
        {
            toggleGate(world, pos, state.get(FACING).rotateYClockwise());

            world.syncWorldEvent(player, state.get(OPEN) ? 1005 : 1011, pos, 0);
            return ActionResult.SUCCESS;
        }
    }

    @Override public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player)
    {
        int count = removeGate(world, pos, state.get(FACING).rotateYClockwise());
        for(int i = 0; i < count; i++)
            Block.dropStacks(state, world, pos, null, player, player.getMainHandStack());
    }

    @Deprecated public float getBlockHardness(BlockState blockState, BlockView world, BlockPos pos)
    {
        if (world == null) return 10f;

        // Combined hardness
        final Direction facing = blockState.get(FACING).rotateYClockwise();
        final BlockPos start = findLowerLeftCorner(world, facing, pos);
        int count = 0;

        for (int hor = 0; hor < maxWidth; hor++)
        {
            final BlockPos hPos = start.offset(facing, hor);
            if (world.getBlockState(hPos).getBlock() != this) break;

            for (int vert = 0; vert < maxHeight; vert++)
            {
                final BlockPos worldPos = hPos.add(0, vert, 0);

                final BlockState state = world.getBlockState(worldPos);
                if (state.getBlock() != this) break;
                else count++;
            }
        }

        return count * hardness;
    }

    @Override public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos)
    {
        return state;
    }

    @Override public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos)
    {
        // Don't allow next to any existing gate
        BlockPos tPos = pos.add(-1, -1, -1);

        for (int x = 0; x < 3; x++)
            for (int y = 0; y < 3; y++)
                for (int z = 0; z < 3; z++)
                {
                    if(world.getBlockState(tPos.add(x, y, z)).getBlock() == this)
                        return false;
                }

        final BlockPos blockpos = pos.down();
        final BlockState blockstate = world.getBlockState(blockpos);
        return blockstate.isSideSolidFullSquare(world, blockpos, Direction.UP);
    }

    @Override public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemstack)
    {
        // Fills the rest of the gate upon placement
        final Direction facing = state.get(FACING).rotateYClockwise();

        // pos
        fillYStates(world, pos, state, itemstack);

        // Left of pos
        int canPlace = maxWidth - 1;
        for (int hor = 1; hor < maxWidth; hor++)
        {
            final BlockPos checkPos = pos.offset(facing.getOpposite(), hor);
            final BlockState checkState = world.getBlockState(checkPos);
            if (checkState.canPlaceAt(world, checkPos) && canPlace > 0 && world.getBlockState(checkPos.offset(facing.getOpposite())).getBlock() != this)
            {
                if (itemstack.getCount() > 1)
                {
                    itemstack.setCount(itemstack.getCount() - 1);
                    world.setBlockState(checkPos, state);
                }
                fillYStates(world, checkPos, state, itemstack);
                canPlace--;
            }
            else break;
        }

        if (canPlace <= 0) return;

        // Right of pos
        for (int hor = 1; hor < maxWidth; hor++)
        {
            final BlockPos checkPos = pos.offset(facing, hor);
            final BlockState checkState = world.getBlockState(checkPos);
            if (checkState.getBlock() != this && checkState.canPlaceAt(world, checkPos) && canPlace > 0
                    && world.getBlockState(checkPos.offset(facing)).getBlock() != this)
            {
                if (itemstack.getCount() > 1)
                {
                    itemstack.setCount(itemstack.getCount() - 1);
                    world.setBlockState(checkPos, state);
                }
                fillYStates(world, checkPos, state, itemstack);
                canPlace--;
            }
            else break;
        }
    }

    @Override public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        if (state.get(OPEN)) return VoxelShapes.empty();

        return getShapeForState(state);
    }

    @Override public VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos)
    {
        return getShapeForState(state);
    }

    @Override public BlockRenderType getRenderType(BlockState state)
    {
        if (state.get(OPEN)) return BlockRenderType.INVISIBLE;
        else return BlockRenderType.MODEL;
    }

    @Override protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        builder.add(HALF, FACING, OPEN, HINGE, POWERED, WATERLOGGED);
    }

    @Override public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return getShapeForState(state);
    }

    @Override public void setOpen(@Nullable Entity entity, World world, BlockState state, BlockPos pos, boolean open)
    {
        BlockState blockstate = world.getBlockState(pos);

        if (blockstate.getBlock() == this && blockstate.get(OPEN) != open)
            toggleGate(world, pos, blockstate.get(FACING).rotateYClockwise());
    }

    /**
     * Mostly redstone stuff for opening
     */
    @Override public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify)
    {
        boolean powered = world.isReceivingRedstonePower(pos);
        if (powered != state.get(OPEN)) setOpen(null,world, state, pos, powered);
    }

    @Nullable @Override public BlockState getPlacementState(ItemPlacementContext ctx)
    {
        BlockPos blockpos = ctx.getBlockPos();

        if (blockpos.getY() < 255 && ctx.getWorld().getBlockState(blockpos.up()).canReplace(ctx))
            return this.getDefaultState().with(FACING, ctx.getPlayerFacing());
        else
            return null;
    }

    /**
     * Removes the whole gate blocks
     *
     * @param world    world to use
     * @param startPos start pos
     * @param facing   gate facing
     * @return amount of removed blocks
     */
    public int removeGate(final World world, final BlockPos startPos, final Direction facing)
    {
        final BlockPos lowerLeftCorner = findLowerLeftCorner(world, facing, startPos);
        int amount = 0;
        // Remove gate
        for (int hor = 0; hor < maxWidth; hor++)
        {
            final BlockPos current = lowerLeftCorner.offset(facing, hor);
            if (world.getBlockState(current).getBlock() != this) break;

            for (int vert = 0; vert < maxHeight; vert++)
            {
                final BlockPos currentPos = current.up(vert);
                if (world.getBlockState(currentPos).getBlock() == this)
                {
                    amount++;
                    world.setBlockState(currentPos, Blocks.AIR.getDefaultState(), 35);
                }
                else break;
            }
        }

        return amount;
    }

    /**
     * Fills gate blocks up to max Y or obstacle
     *
     * @param world world to use
     * @param base  base block we start from
     * @param state state to put
     */
    private void fillYStates(final World world, final BlockPos base, final BlockState state, final ItemStack itemstack)
    {
        for (int vert = 1; vert < maxHeight; vert++)
        {
            final BlockPos checkPos = base.add(0, vert, 0);
            final BlockState checkState = world.getBlockState(checkPos);
            if (checkState.canPlaceAt(world, checkPos) && world.getBlockState(checkPos.up()).getBlock() != this)
            {
                if (itemstack.getCount() > 1)
                {
                    itemstack.setCount(itemstack.getCount() - 1);
                    world.setBlockState(checkPos, state);
                }
            }
            else break;
        }
    }

    /**
     * Gets the right rotated shape
     *
     * @param state state to check
     * @return shape
     */
    private VoxelShape getShapeForState(final BlockState state)
    {
        final Direction direction = state.get(FACING);
        switch (direction)
        {
            case EAST:
            case WEST:
                return E_W_SHAPE;
            case SOUTH:
            case NORTH:
            default:
                return N_S_SHAPE;
        }
    }

    /**
     * Finds the bottom left corner
     *
     * @param facing   direction to check in
     * @param blockPos start pos
     * @return bottom left corner pos
     */
    private BlockPos findLowerLeftCorner(final BlockView world, final Direction facing, final BlockPos blockPos)
    {
        BlockPos tePos = blockPos;

        for (int vert = 0; vert < maxHeight; vert++)
        {
            final BlockPos tempPos = tePos.add(0, -vert, 0);

            if (world.getBlockState(tempPos.down()).getBlock() != this)
            {
                tePos = tempPos;
                break;
            }
        }

        for (int hor = 0; hor < maxWidth; hor++)
            if (world.getBlockState(tePos.offset(facing.getOpposite(), hor + 1)).getBlock() != this)
            {
                tePos = tePos.offset(facing.getOpposite(), hor);
                break;
            }

        return tePos;
    }

    /**
     * Used for activating the gate.
     *
     * @param world        world to use
     * @param clickedBlock block thats clicked/used
     * @param facing       facing to check
     */
    public void toggleGate(final World world, final BlockPos clickedBlock, final Direction facing)
    {
        final BlockPos lowerLeftCorner = findLowerLeftCorner(world, facing, clickedBlock);

        for (int hor = 0; hor < maxWidth; hor++)
        {
            final BlockPos hPos = lowerLeftCorner.offset(facing, hor);

            if (world.getBlockState(hPos).getBlock() != this) break;

            for (int vert = 0; vert < maxHeight; vert++)
            {
                final BlockPos worldPos = hPos.add(0, vert, 0);

                final BlockState state = world.getBlockState(worldPos);
                if (state.getBlock() != this) break;

                // Set top blocks to spikes
                if (world.getBlockState(worldPos.up()).getBlock() != this)
                    WorldUtil.setBlockState(world, worldPos, state.cycle(DoorBlock.HINGE), 2);
                else
                    WorldUtil.setBlockState(world, worldPos, state.cycle(Properties.OPEN), 2);
            }
        }
    }
}
