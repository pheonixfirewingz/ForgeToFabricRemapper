package com.minecolonies.coremod.items;

import org.jetbrains.annotations.NotNull;


/**
 * Class describing the clipboard item.
 */
public class ItemClipboard extends AbstractItemMinecolonies
{
    /**
     * Tag of the colony.
     */
    private static final String TAG_COLONY = "colony";

    /**
     * Sets the name, creative tab, and registers the Clipboard item.
     *
     * @param properties the properties.
     */
    public ItemClipboard(final Item.Properties properties)
    {
        super("clipboard", properties.maxStackSize(STACKSIZE).group(ModCreativeTabs.MINECOLONIES));
    }

    @Override
    @NotNull
    public ActionResultType onItemUse(final ItemUseContext ctx)
    {
        final ItemStack clipboard = ctx.getPlayer().getHeldItem(ctx.getHand());

        final CompoundNBT compound = checkForCompound(clipboard);
        final TileEntity entity = ctx.getWorld().getTileEntity(ctx.getPos());

        if (entity instanceof TileEntityColonyBuilding)
        {
            compound.putInt(TAG_COLONY, ((AbstractTileEntityColonyBuilding) entity).getColonyId());
            if (!ctx.getWorld().isRemote)
            {
                LanguageHandler.sendPlayerMessage(
                        ctx.getPlayer(),
                        TranslationConstants.COM_MINECOLONIES_CLIPBOARD_COLONY_SET,
                        ((AbstractTileEntityColonyBuilding) entity).getColony().getName());
            }
        }
        else if (ctx.getWorld().isRemote)
        {
            openWindow(compound, ctx.getWorld(), ctx.getPlayer());
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

        if (!worldIn.isRemote) {
            return new ActionResult<>(ActionResultType.SUCCESS, clipboard);
        }

        openWindow(checkForCompound(clipboard), worldIn, playerIn);

        return new ActionResult<>(ActionResultType.SUCCESS, clipboard);
    }

    /**
     * Check for the compound and return it. If not available create and return it.
     *
     * @param clipboard the clipboard to check for.
     * @return the compound of the clipboard.
     */
    private static CompoundNBT checkForCompound(final ItemStack clipboard)
    {
        if (!clipboard.hasTag()) clipboard.setTag(new CompoundNBT());
        return clipboard.getTag();
    }

    /**
     * Opens the clipboard window if there is a valid colony linked
     * @param compound the item compound
     * @param player the player entity opening the window
     */
    private static void openWindow(CompoundNBT compound, World world, PlayerEntity player)
    {
        if (compound.keySet().contains(TAG_COLONY))
        {
            final IColonyView colonyView = IColonyManager.getInstance().getColonyView(compound.getInt(TAG_COLONY), world.getDimensionKey());
            if (colonyView != null) MineColonies.proxy.openClipboardWindow(colonyView);
        }
        else
        {
            player.sendStatusMessage(new TranslationTextComponent(TranslationConstants.COM_MINECOLONIES_CLIPBOARD_NEED_COLONY), true);
        }
    }
}
