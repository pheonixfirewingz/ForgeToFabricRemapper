package com.minecolonies.coremod.items;


import java.util.Collection;


/**
 * Beekeeper Scepter Item class. Used to give tasks to Beekeeper.
 */
public class ItemScepterBeekeeper extends AbstractItemMinecolonies
{
    /**
     * BeekeeperScepter constructor. Sets max stack to 1, like other tools.
     *
     * @param properties the properties.
     */
    public ItemScepterBeekeeper(final Properties properties)
    {
        super("scepterbeekeeper", properties.maxStackSize(1));
    }

    @Override
    public ActionResultType onItemUse(final ItemUseContext useContext)
    {
        // if server world, do nothing
        if (useContext.getWorld().isRemote)
        {
            return ActionResultType.FAIL;
        }

        final PlayerEntity player = useContext.getPlayer();

        final ItemStack scepter = useContext.getPlayer().getHeldItem(useContext.getHand());
        final CompoundNBT compound = scepter.getOrCreateTag();

        final IColony colony = IColonyManager.getInstance().getColonyByWorld(compound.getInt(TAG_ID), useContext.getWorld());
        final BlockPos hutPos = BlockPosUtil.read(compound, TAG_POS);
        final IBuilding hut = colony.getBuildingManager().getBuilding(hutPos);
        final BuildingBeekeeper building = (BuildingBeekeeper) hut;

        if (useContext.getWorld().getBlockState(useContext.getPos()).getBlock() instanceof BeehiveBlock)
        {

            final Collection<BlockPos> positions = building.getHives();

            final BlockPos pos = useContext.getPos();
            if (positions.contains(pos))
            {
                LanguageHandler.sendPlayerMessage(useContext.getPlayer(), "item.minecolonies.scepterbeekeeper.removehive");
                building.removeHive(pos);
            }
            else
            {
                if (positions.size() < building.getMaximumHives())
                {
                    LanguageHandler.sendPlayerMessage(useContext.getPlayer(), "item.minecolonies.scepterbeekeeper.addhive");
                    building.addHive(pos);
                }
                if (positions.size() >= building.getMaximumHives())
                {
                    LanguageHandler.sendPlayerMessage(useContext.getPlayer(), "item.minecolonies.scepterbeekeeper.maxhives");
                    player.inventory.removeStackFromSlot(player.inventory.currentItem);
                }
            }
        }
        else
        {
            player.inventory.removeStackFromSlot(player.inventory.currentItem);
        }

        return super.onItemUse(useContext);
    }
}
