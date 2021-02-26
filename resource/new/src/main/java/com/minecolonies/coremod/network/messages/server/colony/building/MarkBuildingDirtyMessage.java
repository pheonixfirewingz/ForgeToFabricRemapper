package com.minecolonies.coremod.network.messages.server.colony.building;


/**
 * Send a message to the server to mark the building as dirty. Created: January 20, 2017
 *
 * @author xavierh
 */
public class MarkBuildingDirtyMessage extends AbstractBuildingServerMessage<IBuilding>
{
    /**
     * Empty constructor used when registering the
     */
    public MarkBuildingDirtyMessage()
    {
        super();
    }

    @Override
    protected void toBytesOverride(final PacketBuffer buf)
    {

    }

    @Override
    protected void fromBytesOverride(final PacketBuffer buf)
    {

    }

    public MarkBuildingDirtyMessage(final IBuildingView building)
    {
        super(building);
    }

    @Override
    protected void onExecute(final NetworkEvent.Context ctxIn, final boolean isLogicalServer, final IColony colony, final IBuilding building)
    {
        building.getTileEntity().markDirty();
    }
}
