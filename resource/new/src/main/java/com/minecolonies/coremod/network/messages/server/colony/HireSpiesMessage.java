package com.minecolonies.coremod.network.messages.server.colony;



/**
 * Message for hiring spies at the cost of gold.
 */
public class HireSpiesMessage extends AbstractColonyServerMessage
{
    public HireSpiesMessage()
    {
    }

    public HireSpiesMessage(final IColony colony)
    {
        super(colony);
    }

    @Override
    protected void onExecute(final NetworkEvent.Context ctxIn, final boolean isLogicalServer, final IColony colony)
    {
        final PlayerEntity player = ctxIn.getSender();
        if (player == null)
        {
            return;
        }

        if (InventoryUtils.getItemCountInItemHandler(new InvWrapper(player.inventory), stack -> stack.getItem() == Items.GOLD_INGOT) > SPIES_GOLD_COST)
        {
            InventoryUtils.reduceStackInItemHandler(new InvWrapper(player.inventory), new ItemStack(Items.GOLD_INGOT), SPIES_GOLD_COST);
            colony.getRaiderManager().setSpiesEnabled(true);
            colony.markDirty();
        }
    }

    @Override
    protected void toBytesOverride(final PacketBuffer buf)
    {


    }

    @Override
    protected void fromBytesOverride(final PacketBuffer buf)
    {

    }
}
