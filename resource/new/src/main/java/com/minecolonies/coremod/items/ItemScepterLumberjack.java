package com.minecolonies.coremod.items;



/**
 * Lumberjack Scepter Item class. Used to give tasks to Lumberjacks.
 */
public class ItemScepterLumberjack extends AbstractItemMinecolonies
{
    private static final String NBT_START_POS = Constants.MOD_ID + ":" + "start_pos";
    private static final String NBT_END_POS   = Constants.MOD_ID + ":" + "end_pos";

    private boolean hasSetFirstPosition = false;

    /**
     * LumberjackScepter constructor. Sets max stack to 1, like other tools.
     *
     * @param properties the properties.
     */
    public ItemScepterLumberjack(final Properties properties)
    {
        super("scepterlumberjack", properties.maxStackSize(1));
    }

    @Override
    public ActionResultType onItemUse(final ItemUseContext useContext)
    {
        // if server world, do nothing
        if (useContext.getWorld().isRemote)
        {
            return ActionResultType.FAIL;
        }

        final ItemStack scepter = useContext.getPlayer().getHeldItem(useContext.getHand());
        if (!scepter.hasTag())
        {
            scepter.setTag(new CompoundNBT());
        }
        final CompoundNBT compound = scepter.getTag();

        if (!hasSetFirstPosition)
        {
            LanguageHandler.sendPlayerMessage(useContext.getPlayer(), "item.minecolonies.scepterlumberjack.usedstart");
            setPosition(compound, NBT_START_POS, useContext.getPos());
        }
        else
        {
            LanguageHandler.sendPlayerMessage(useContext.getPlayer(), "item.minecolonies.scepterlumberjack.usedend");
            setPosition(compound, NBT_END_POS, useContext.getPos());
            storeRestrictedArea(useContext.getPlayer(), useContext.getHand(), useContext.getWorld());
        }

        return super.onItemUse(useContext);
    }

    private void storeRestrictedArea(final PlayerEntity player, final Hand hand, final World worldIn)
    {
        final ItemStack scepter = player.getHeldItem(hand);
        if (!scepter.hasTag())
        {
            scepter.setTag(new CompoundNBT());
        }

        final CompoundNBT compound = scepter.getTag();

        final BlockPos startRestriction = BlockPosUtil.read(compound, NBT_START_POS);
        final BlockPos endRestriction = BlockPosUtil.read(compound, NBT_END_POS);

        // Check restricted area isn't too large
        final int minX = Math.min(startRestriction.getX(), endRestriction.getX());
        final int minZ = Math.min(startRestriction.getZ(), endRestriction.getZ());
        final int maxX = Math.max(startRestriction.getX(), endRestriction.getX());
        final int maxZ = Math.max(startRestriction.getZ(), endRestriction.getZ());

        final int distX = maxX - minX;
        final int distZ = maxZ - minZ;

        final int area = distX * distZ;

        final int radius = EntityAIWorkLumberjack.SEARCH_RANGE;
        final double maxArea = 3.14 * Math.pow(radius, 2);

        if (area > maxArea)
        {
            LanguageHandler.sendPlayerMessage(player, "item.minecolonies.scepterlumberjack.restrictiontoobig", area, maxArea);
            return;
        }

        LanguageHandler.sendPlayerMessage(player, "item.minecolonies.scepterlumberjack.restrictionset", area, maxArea);

        final IColony colony = IColonyManager.getInstance().getColonyByWorld(compound.getInt(TAG_ID), worldIn);
        final BlockPos hutPos = BlockPosUtil.read(compound, TAG_POS);
        final IBuilding hut = colony.getBuildingManager().getBuilding(hutPos);

        final AbstractFilterableListBuilding abstractBuilding = (AbstractFilterableListBuilding) hut;

        final BuildingLumberjack lumberjackBuilding = (BuildingLumberjack) abstractBuilding;

        lumberjackBuilding.setRestrictedArea(startRestriction, endRestriction);

        player.inventory.removeStackFromSlot(player.inventory.currentItem);
    }

    private void setPosition(final CompoundNBT compound, final String NBT, final BlockPos pos)
    {
        hasSetFirstPosition = !hasSetFirstPosition;
        BlockPosUtil.write(compound, NBT, pos);
    }
}
