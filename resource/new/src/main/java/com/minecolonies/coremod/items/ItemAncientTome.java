package com.minecolonies.coremod.items;



/**
 * Class describing the Ancient Tome item.
 */
public class ItemAncientTome extends AbstractItemMinecolonies
{
    /**
     * Sets the name, creative tab, and registers the Ancient Tome item.
     *
     * @param properties the properties.
     */
    public ItemAncientTome(final Properties properties)
    {
        super("ancienttome", properties.maxStackSize(STACKSIZE).group(ModCreativeTabs.MINECOLONIES));
    }

    @Override
    public void inventoryTick(final ItemStack stack, final World worldIn, final Entity entityIn, final int itemSlot, final boolean isSelected)
    {
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
        if (!worldIn.isRemote)
        {
            final IColony colony = IColonyManager.getInstance().getClosestColony(worldIn, new BlockPos(entityIn.getPositionVec()));
            final CompoundNBT tag = new CompoundNBT();

            if (colony != null)
            {
                tag.putBoolean(NbtTagConstants.TAG_RAID_WILL_HAPPEN, colony.getRaiderManager().willRaidTonight());
            }
            else
            {
                tag.putBoolean(NbtTagConstants.TAG_RAID_WILL_HAPPEN, false);
            }
            stack.setTag(tag);
        }
    }

    public boolean hasEffect(final ItemStack stack)
    {
        return stack.getTag() != null && stack.getTag().getBoolean(NbtTagConstants.TAG_RAID_WILL_HAPPEN);
    }
}
