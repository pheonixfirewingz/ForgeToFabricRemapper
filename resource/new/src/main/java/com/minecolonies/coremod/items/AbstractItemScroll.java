package com.minecolonies.coremod.items;

import org.jetbrains.annotations.NotNull;


/**
 * Scroll items base class, does colony registering/checks.
 */
public abstract class AbstractItemScroll extends AbstractItemMinecolonies
{
    public static final String TAG_COLONY_DIM       = "colony_dim";
    public static final String TAG_BUILDING_POS     = "building_pos";
    public static final int    FAIL_RESPONSES_TOTAL = 10;

    /**
     * Sets the name, creative tab, and registers the item.
     *
     * @param name       The name of this item
     * @param properties the properties.
     */
    public AbstractItemScroll(final String name, final Properties properties)
    {
        super(name, properties);
    }

    @Override
    public int getUseDuration(ItemStack itemStack)
    {
        return 32;
    }

    @Override
    public UseAction getUseAction(ItemStack itemStack)
    {
        return UseAction.BOW;
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack itemStack, World world, LivingEntity entityLiving)
    {
        if (!(entityLiving instanceof ServerPlayerEntity) || world.isRemote)
        {
            return itemStack;
        }

        final ServerPlayerEntity player = (ServerPlayerEntity) entityLiving;

        if (!needsColony())
        {
            return onItemUseSuccess(itemStack, world, player);
        }

        final IColony colony = getColony(itemStack);
        if (colony == null)
        {
            player.sendStatusMessage(new TranslationTextComponent("minecolonies.scroll.needcolony"), true);
            return itemStack;
        }

        if (!colony.getPermissions().hasPermission(player, Action.RIGHTCLICK_BLOCK))
        {
            LanguageHandler.sendPlayerMessage(player, "minecolonies.scroll.nopermission");
            return itemStack;
        }

        return onItemUseSuccess(itemStack, world, player);
    }

    /**
     * Called when the item gets used
     *
     * @param itemStack stack thats used
     * @param world     world its used in
     * @param player    player its used by
     * @return stack
     */
    protected abstract ItemStack onItemUseSuccess(final ItemStack itemStack, final World world, final ServerPlayerEntity player);

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand)
    {
        ItemStack itemStack = player.getHeldItem(hand);
        player.setActiveHand(hand);

        // Sneak rightclick
        return new ActionResult<>(ActionResultType.FAIL, itemStack);
    }

    @Override
    @NotNull
    public ActionResultType onItemUse(ItemUseContext ctx)
    {
        // Right click on block
        if (ctx.getWorld().isRemote || !ctx.getPlayer().isSneaking() || !needsColony())
        {
            return ActionResultType.PASS;
        }

        final TileEntity te = ctx.getWorld().getTileEntity(ctx.getPos());
        final ItemStack scroll = ctx.getPlayer().getHeldItem(ctx.getHand());
        final CompoundNBT compound = checkForCompound(scroll);
        if (te instanceof TileEntityColonyBuilding)
        {
            compound.putInt(TAG_COLONY_ID, ((AbstractTileEntityColonyBuilding) te).getColonyId());
            compound.putString(TAG_COLONY_DIM, ((AbstractTileEntityColonyBuilding) te).getColony().getWorld().getDimensionKey().getLocation().toString());
            BlockPosUtil.write(compound, TAG_BUILDING_POS, ctx.getPos());
            LanguageHandler.sendPlayerMessage(ctx.getPlayer(),
              "minecolonies.scroll.registered",
              ((AbstractTileEntityColonyBuilding) te).getColony().getName());
        }

        return ActionResultType.SUCCESS;
    }

    /**
     * Whether this items need to register to a colony
     *
     * @return true if so
     */
    protected abstract boolean needsColony();

    private static CompoundNBT checkForCompound(final ItemStack item)
    {
        if (!item.hasTag())
        {
            item.setTag(new CompoundNBT());
        }
        return item.getTag();
    }

    /**
     * Get the colony from the stack
     *
     * @param stack to use
     * @return colony
     */
    protected IColony getColony(final ItemStack stack)
    {
        if (!stack.hasTag() || !stack.getTag().contains(TAG_COLONY_ID) || !stack.getTag().contains(TAG_COLONY_DIM))
        {
            return null;
        }

        return IColonyManager.getInstance().getColonyByDimension(stack.getTag().getInt(TAG_COLONY_ID), RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(stack.getTag().getString(TAG_COLONY_DIM))));
    }

    /**
     * Get the colony view from the stack
     *
     * @param stack to use
     * @return colony
     */
    protected IColony getColonyView(final ItemStack stack)
    {
        if (!stack.hasTag() || !stack.getTag().contains(TAG_COLONY_ID) || !stack.getTag().contains(TAG_COLONY_DIM))
        {
            return null;
        }

        return IColonyManager.getInstance().getColonyView(stack.getTag().getInt(TAG_COLONY_ID), RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(stack.getTag().getString(TAG_COLONY_DIM))));
    }
}
