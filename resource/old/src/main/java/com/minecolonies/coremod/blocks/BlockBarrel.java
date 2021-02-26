package com.minecolonies.coremod.blocks;

import com.minecolonies.coremod.MinecoloniesBlocks;
import com.minecolonies.coremod.blocks.types.BarrelType;
import com.minecolonies.api.tileentities.AbstractTileEntityBarrel;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.state.*;
import net.minecraft.state.property.*;
import net.minecraft.util.*;
import net.minecraft.util.hit.*;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.*;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;

public class BlockBarrel extends Block implements BlockEntityProvider
{
    /**
     * The hardness this block has.
     */
    private static final float  BLOCK_HARDNESS = 5F;
    /**
     * The resistance this block has.
     */
    private static final float  RESISTANCE     = 1F;

    public static final EnumProperty<BarrelType> VARIANT = EnumProperty.of("variant", BarrelType.class);

    /**
     * The position it faces.
     */
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;


    public BlockBarrel()
    {
        super(Settings.of(Material.WOOD).strength(BLOCK_HARDNESS, RESISTANCE));
        this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH).with(VARIANT, BarrelType.ZERO));
    }

    @Override protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        builder.add(FACING, VARIANT);
    }

    @Override public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        final ItemStack itemstack = player.getInventory().getMainHandStack();
        final BlockEntity te = world.getBlockEntity(pos);
        //fixme: need title entity
        /*if (te instanceof TEBarrel && !world.isClient())
        {
            ((TEBarrel) te).useBarrel(player, itemstack);
            ((TEBarrel) te).updateBlock(world);
        }*/

        return ActionResult.SUCCESS;
    }

    @Override public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return VoxelShapes.cuboid(0D, 0D, 0D, 1D, 1.5D, 1D);
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

    @Nullable @Override public BlockState getPlacementState(ItemPlacementContext ctx)
    {
        return super.getPlacementState(ctx).with(FACING, ctx.getPlayerFacing());
    }

    /**
     * @deprecated (Remove this as soon as minecraft offers anything better).
     */
    @Override public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos)
    {
        return !world.isAir(pos.down()) && world.getBlockState(pos.down()).getBlock() != MinecoloniesBlocks.barrel_block;
    }

    public static BlockState changeStateOverFullness(@NotNull final AbstractTileEntityBarrel te, @NotNull final BlockState blockState)
    {
        /*
         * 12.8 -> the number of items needed to go up on a state (having 6 filling states)
         * So items/12.8 -> meta of the state we should get
         */
        BarrelType type = BarrelType.byMetadata((int) Math.round(te.getItems() / 12.8));

        /*
         * We check if the barrel is marked as empty but it have items inside. If so, means that it
         * does not have all the items needed to go on TWENTY state, but we need to mark it so the player
         * knows it have some items inside
         */

        if(type.equals(BarrelType.ZERO) && te.getItems() > 0)
        {
            type = BarrelType.TWENTY;
        } else if(te.getItems() == AbstractTileEntityBarrel.MAX_ITEMS)
        {
            type = BarrelType.WORKING;
        }
        if(te.isDone())
        {
            type = BarrelType.DONE;
        }

        return blockState.with(VARIANT, type).with(FACING, blockState.get(FACING));
    }

    @Nullable @Override public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return null;
    }
}
