package com.minecolonies.coremod.network.messages.server.colony.building.postbox;

import org.jetbrains.annotations.NotNull;

/**
 * Creates a request from the postbox.
 */
public class PostBoxRequestMessage extends AbstractBuildingServerMessage<PostBox>
{
    /**
     * How many item need to be transfer from the player inventory to the building chest.
     */
    private ItemStack itemStack;

    /*
     * Whether to deliver what's currently available or entire request
     */
    private boolean deliverAvailable;

    private int reqQuantity;

    /**
     * Empty constructor used when registering the
     */
    public PostBoxRequestMessage()
    {
        super();
    }

    /**
     * Creates a Transfer Items request
     *
     * @param building  AbstractBuilding of the request.
     * @param itemStack to be take from the player for the building
     * @param quantity  of item needed to be transfered
     */
    public PostBoxRequestMessage(@NotNull final AbstractBuildingView building, final ItemStack itemStack, final int quantity, final boolean deliverAvailable)
    {
        super(building);
        this.itemStack = itemStack;
        reqQuantity = quantity;
        this.deliverAvailable = deliverAvailable;
    }

    @Override
    public void toBytesOverride(@NotNull final PacketBuffer buf)
    {

        buf.writeItemStack(itemStack);
        buf.writeBoolean(deliverAvailable);
        buf.writeInt(reqQuantity);
    }

    @Override
    public void fromBytesOverride(@NotNull final PacketBuffer buf)
    {

        itemStack = buf.readItemStack();
        deliverAvailable = buf.readBoolean();
        reqQuantity = buf.readInt();
    }

    @Override
    protected void onExecute(
      final NetworkEvent.Context ctxIn, final boolean isLogicalServer, final IColony colony, final PostBox building)
    {

        final int minCount = (deliverAvailable) ? 1 : reqQuantity;
        Stack requestStack = new Stack(itemStack, reqQuantity, minCount);

        building.createRequest(requestStack, false);
    }
}
