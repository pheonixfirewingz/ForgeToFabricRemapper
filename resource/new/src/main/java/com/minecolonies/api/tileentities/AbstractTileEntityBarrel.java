package com.minecolonies.api.tileentities;


public abstract class AbstractTileEntityBarrel extends TileEntity implements ITickableTileEntity
{
    /**
     * The number of items it needs to start composting
     */


    public AbstractTileEntityBarrel(final TileEntityType<?> tileEntityTypeIn)
    {
        super(tileEntityTypeIn);
    }

    /**
     * Returns the number of items that the block contains
     *
     * @return the number of items
     */
    public abstract int getItems();

    public abstract boolean isDone();

    public abstract boolean checkIfWorking();

    public abstract boolean addItem(ItemStack item);

    public abstract ItemStack retrieveCompost(double multiplier);
}
