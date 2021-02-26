package com.minecolonies.coremod.network.messages.server.colony.building;

import org.jetbrains.annotations.NotNull;

/**
 * Set a new block to the minimum stock list.
 */
public class AddMinimumStockToBuildingMessage extends AbstractBuildingServerMessage<IBuilding>
{
    /**
     * How many item need to be transfer from the player inventory to the building chest.
     */
    private ItemStack itemStack;

    /**
     * How many item need to be transfer from the player inventory to the building chest.
     */
    private int quantity;

    /**
     * Empty constructor used when registering the
     */
    public AddMinimumStockToBuildingMessage()
    {
        super();
    }

    /**
     * Creates a Transfer Items request
     *
     * @param itemStack to be take from the player for the building
     * @param quantity  of item needed to be transfered
     * @param building  the building we're executing on.
     */
    public AddMinimumStockToBuildingMessage(final IBuildingView building, final ItemStack itemStack, final int quantity)
    {
        super(building);
        this.itemStack = itemStack;
        this.quantity = quantity;
    }

    @Override
    public void fromBytesOverride(@NotNull final PacketBuffer buf)
    {

        itemStack = buf.readItemStack();
        quantity = buf.readInt();
    }

    @Override
    public void toBytesOverride(@NotNull final PacketBuffer buf)
    {
        buf.writeItemStack(itemStack);
        buf.writeInt(quantity);
    }

    @Override
    public void onExecute(final NetworkEvent.Context ctxIn, final boolean isLogicalServer, final IColony colony, final IBuilding building)
    {
        building.addMinimumStock(itemStack, quantity);
    }
}