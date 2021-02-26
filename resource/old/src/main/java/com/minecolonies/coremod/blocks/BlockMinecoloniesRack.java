package com.minecolonies.coremod.blocks;

import com.minecolonies.api.colony.permissions.Action;
import com.minecolonies.api.util.*;
import com.minecolonies.coremod.blocks.types.*;
import com.minecolonies.coremod.colony.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.loot.context.*;
import net.minecraft.server.network.*;
import net.minecraft.server.world.*;
import net.minecraft.state.*;
import net.minecraft.state.property.*;
import net.minecraft.util.*;
import net.minecraft.util.hit.*;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.*;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Block for the shelves of the warehouse.
 */
public class BlockMinecoloniesRack extends Block
{

    /**
     * The hardness this block has.
     */
    private static final float BLOCK_HARDNESS = 10.0F;

    /**
     * The resistance this block has.
     */
    private static final float RESISTANCE = Float.POSITIVE_INFINITY;

    public static final EnumProperty<RackType> VARIANT =  EnumProperty.of("variant", RackType.class);
    public static final int DEFAULT_META = RackType.DEFAULT.getMetadata();
    public static final int FULL_META = RackType.FULL.getMetadata();
    /**
     * The position it faces.
     */
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    /**
     * Smaller shape.
     */
    private static final VoxelShape SHAPE = VoxelShapes.cuboid(0.1, 0.1, 0.1, 0.9, 0.9, 0.9);

    public BlockMinecoloniesRack()
    {
        super(Settings.of(Material.WOOD).strength(BLOCK_HARDNESS, RESISTANCE).noCollision());
        this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH).with(VARIANT, RackType.DEFAULT));
    }
    @Override protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        builder.add(FACING, VARIANT);
    }

    @Override public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return SHAPE;
    }

    @Nullable @Override public BlockState getPlacementState(ItemPlacementContext ctx)
    {
        final World worldIn = ctx.getWorld();
        final BlockPos pos = ctx.getBlockPos();
        final BlockState state = getDefaultState();
        final BlockEntity entity = worldIn.getBlockEntity(pos);

        if (!(entity instanceof BlockEntityRack))
            return super.getPlacementState(ctx);

        return getPlacementState(state, entity, pos);
    }

    /**
     * Get the statement ready.
     *
     * @param state  the state to place.
     * @param entity the BlockEntity.
     * @param pos    the position.
     * @return the next state.
     */
    public static BlockState getPlacementState(final BlockState state, final BlockEntity entity, final BlockPos pos)
    {
        final AbstractBlockEntityRack rack = (AbstractBlockEntityRack) entity;
        if (rack.isEmpty() && (rack.getOtherChest() == null || rack.getOtherChest().isEmpty()))
        {
            if (rack.getOtherChest() != null)
            {
                if (rack.isMain())
                {
                    return state.with(VARIANT, RackType.DEFAULTDOUBLE).with(FACING, BlockPosUtil.getFacing(rack.getNeighbor(), pos));
                }
                else
                {
                    return state.with(VARIANT, RackType.EMPTYAIR);
                }
            }
            else
            {
                return state.with(VARIANT, RackType.DEFAULT);
            }
        }
        else
        {
            if (rack.getOtherChest() != null)
            {
                if (rack.isMain())
                {
                    return state.with(VARIANT, RackType.FULLDOUBLE)
                             .with(FACING, BlockPosUtil.getFacing(rack.getNeighbor(), pos));
                }
                else
                {
                    return state.with(VARIANT, RackType.EMPTYAIR);
                }
            }
            else
            {
                return state.with(VARIANT, RackType.FULL);
            }
        }
    }

    /**
     * Convert the BlockState into the correct metadata value.
     *
     * @deprecated (Remove this as soon as minecraft offers anything better).
     */
    @Override public BlockState rotate(BlockState state, BlockRotation rotation)
    {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    /**
     * @deprecated (Remove this as soon as minecraft offers anything better).
     */
    @Override public BlockState mirror(BlockState state, BlockMirror mirror)
    {
        return  state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos)
    {

        if (state.getBlock() instanceof BlockMinecoloniesRack || state.getBlock() instanceof BlockMinecoloniesRack)
        {
            final BlockEntity rack = world.getBlockEntity(pos);
            if (rack instanceof BlockEntityRack)
            {
                ((AbstractBlockEntityRack) rack).neighborChanged(neighborPos);
            }
            final BlockEntity rack2 = world.getBlockEntity(neighborPos);
            if (rack2 instanceof BlockEntityRack)
            {
                ((AbstractBlockEntityRack) rack2).neighborChanged(pos);
            }
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void spawnAdditionalDrops(final BlockState state, final ServerWorld worldIn, final BlockPos pos, final ItemStack stack)
    {
        final BlockEntity BlockEntity = worldIn.getBlockEntity(pos);
        if (BlockEntity instanceof BlockEntityRack)
        {
            final IItemHandler handler = ((AbstractBlockEntityRack) BlockEntity).getInventory();
            InventoryUtils.dropItemHandler(handler, worldIn, pos.getX(), pos.getY(), pos.getZ());
        }
        super.spawnAdditionalDrops(state, worldIn, pos, stack);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        final Colony colony = ColonyManager.getInstance().getColonyByPosFromWorld(world, pos);
        final BlockEntity BlockEntity = world.getBlockEntity(pos);

        if ((colony == null || colony.getPermissions().hasPermission(player, Action.ACCESS_HUTS))
                && BlockEntity instanceof BlockEntityRack)
        {
            final BlockEntityRack rack = (BlockEntityRack) BlockEntity;
            if (!worldIn.isRemote)
            {
                NetworkHooks.openGui((ServerPlayerEntity) player,
                        rack,
                        buf -> buf.writeBlockPos(rack.getPos()).writeBlockPos(rack.getOtherChest() == null ? BlockPos.ZERO : rack.getOtherChest().getPos()));
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }


    @Override
    public void onBlockPlacedBy(final World worldIn, final BlockPos pos, final BlockState state, @Nullable final LivingEntity placer, final ItemStack stack)
    {
        BlockState tempState = state;
        tempState = tempState.with(VARIANT, RackType.DEFAULT);
        if (placer != null)
        {
            tempState = tempState.with(FACING, placer.getHorizontalFacing().getOpposite());
        }

        worldIn.setBlockState(pos, tempState, 2);
    }

    @Override
    public List<ItemStack> getDrops(final BlockState state, final LootContext.Builder builder)
    {
        final List<ItemStack> drops = new ArrayList<>();
        drops.add(new ItemStack(this, 1));
        return drops;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
        if (state.getBlock() != newState.getBlock())
        {
            BlockEntity BlockEntity = world.getBlockEntity(pos);
            if(BlockEntity instanceof BlockEntityRack)
            {
                BlockEntityRack BlockEntityRack = (BlockEntityRack) BlockEntity;
                InventoryUtils.dropItemHandler(BlockEntityRack.getInventory(),
                        world,
                        BlockEntityRack.getPos().getX(),
                        BlockEntityRack.getPos().getY(),
                        BlockEntityRack.getPos().getZ());
                worldIn.updateComparatorOutputLevel(pos, this);
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    public static boolean shouldBlockBeReplacedWithRack(final Block block)
    {
        return block == Blocks.CHEST || block instanceof BlockMinecoloniesRack;
    }
}
