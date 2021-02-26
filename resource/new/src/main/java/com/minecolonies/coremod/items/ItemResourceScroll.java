package com.minecolonies.coremod.items;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;


/**
 * Class describing the resource scroll item.
 */
public class ItemResourceScroll extends AbstractItemMinecolonies
{
    /**
     * Sets the name, creative tab, and registers the resource scroll item.
     *
     * @param properties the properties.
     */
    public ItemResourceScroll(final Item.Properties properties)
    {
        super("resourcescroll", properties.maxStackSize(STACKSIZE).group(ModCreativeTabs.MINECOLONIES));
    }

    /**
     * Used when clicking on block in world.
     *
     * @param ctx the context of use.
     * @return the result
     */
    @Override
    @NotNull
    public ActionResultType onItemUse(ItemUseContext ctx)
    {
        final ItemStack scroll = ctx.getPlayer().getHeldItem(ctx.getHand());

        final CompoundNBT compound = checkForCompound(scroll);
        TileEntity entity = ctx.getWorld().getTileEntity(ctx.getPos());

        if (entity instanceof TileEntityColonyBuilding)
        {
            compound.putInt(TAG_COLONY_ID, ((AbstractTileEntityColonyBuilding) entity).getColonyId());
            BlockPosUtil.write(compound, TAG_BUILDER, ((AbstractTileEntityColonyBuilding) entity).getPosition());

            if (!ctx.getWorld().isRemote)
            {
                LanguageHandler.sendPlayerMessage(ctx.getPlayer(),
                  TranslationConstants.COM_MINECOLONIES_SCROLL_BUILDER_SET,
                  ((AbstractTileEntityColonyBuilding) entity).getColony().getName());
            }
        }
        else if (ctx.getWorld().isRemote)
        {
            openWindow(compound, ctx.getPlayer());
        }

        return ActionResultType.SUCCESS;
    }

    /**
     * Handles mid air use.
     *
     * @param worldIn  the world
     * @param playerIn the player
     * @param hand     the hand
     * @return the result
     */
    @Override
    @NotNull
    public ActionResult<ItemStack> onItemRightClick(
      final World worldIn,
      final PlayerEntity playerIn,
      final Hand hand)
    {
        final ItemStack clipboard = playerIn.getHeldItem(hand);

        if (!worldIn.isRemote)
        {
            return new ActionResult<>(ActionResultType.SUCCESS, clipboard);
        }

        openWindow(checkForCompound(clipboard), playerIn);

        return new ActionResult<>(ActionResultType.SUCCESS, clipboard);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        if (worldIn == null) return;

        final CompoundNBT compound = checkForCompound(stack);
        final int colonyId = compound.getInt(TAG_COLONY_ID);
        final BlockPos builderPos = BlockPosUtil.read(compound, TAG_BUILDER);

        final IColonyView colonyView = IColonyManager.getInstance().getColonyView(colonyId, worldIn.getDimensionKey());
        if (colonyView != null)
        {
            final IBuildingView buildingView = colonyView.getBuilding(builderPos);
            if (buildingView instanceof BuildingBuilder.View)
            {
                String name = ((BuildingBuilder.View) buildingView).getWorkerName();
                tooltip.add(name != null && !name.trim().isEmpty()
                  ? new StringTextComponent(TextFormatting.DARK_PURPLE + name)
                  : new TranslationTextComponent(TranslationConstants.COM_MINECOLONIES_SCROLL_NO_BUILDER));
            }
        }
    }

    /**
     * Check for the compound and return it. If not available create and return it.
     *
     * @param item the item to check in for.
     * @return the compound of the item.
     */
    private static CompoundNBT checkForCompound(final ItemStack item)
    {
        if (!item.hasTag())
        {
            item.setTag(new CompoundNBT());
        }
        return item.getTag();
    }

    /**
     * Opens the scroll window if there is a valid builder linked
     * @param compound the item compound
     * @param player the player entity opening the window
     */
    private static void openWindow(CompoundNBT compound, PlayerEntity player)
    {
        if (compound.keySet().contains(TAG_COLONY_ID) && compound.keySet().contains(TAG_BUILDER))
        {
            final int colonyId = compound.getInt(TAG_COLONY_ID);
            final BlockPos builderPos = BlockPosUtil.read(compound, TAG_BUILDER);
            MineColonies.proxy.openResourceScrollWindow(colonyId, builderPos);
        }
        else
        {
            player.sendStatusMessage(new TranslationTextComponent(TranslationConstants.COM_MINECOLONIES_SCROLL_NEED_BUILDER), true);
        }
    }
}
