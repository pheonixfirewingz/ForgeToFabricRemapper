package com.minecolonies.coremod.items;

import com.minecolonies.api.creativetab.ModCreativeTabs;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.coremod.util.MineColoniesItemSettings;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.*;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Class used to handle the compost item.
 */
public class ItemCompost extends Item
{

    /***
     * Constructor for the ItemCompost
     * @param properties the properties.
     */
    public ItemCompost()
    {
        super(new MineColoniesItemSettings().maxCount(Constants.STACKSIZE));
    }

    @Override
    public ActionResult onItemUse(final ItemUseContext ctx)
    {
        final ItemStack itemstack = ctx.getPlayer().getHeldItem(ctx.getHand());
        if (applyBonemeal(itemstack, ctx.getWorld(), ctx.getPos(), ctx.getPlayer()))
        {
            if (!ctx.getWorld().isRemote)
            {
                ctx.getWorld().playEvent(2005, ctx.getPos(), 0);
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }

    public static boolean applyBonemeal(final ItemStack stack, final World worldIn, final BlockPos target, final PlayerEntity player)
    {
        final BlockState BlockState = worldIn.getBlockState(target);
            if (BlockState.getBlock() instanceof IGrowable)
            {
                final IGrowable igrowable = (IGrowable) BlockState.getBlock();
                if (igrowable.canGrow(worldIn, target, BlockState, worldIn.isRemote))
                {
                    if (!worldIn.isRemote)
                    {
                        if (igrowable.canUseBonemeal(worldIn, worldIn.rand, target, BlockState))
                        {
                            igrowable.grow((ServerWorld) worldIn, worldIn.rand, target, BlockState);
                        }
                        stack.decrement(1);
                    }
                    return true;
                }
            }
            return false;
    }
}


