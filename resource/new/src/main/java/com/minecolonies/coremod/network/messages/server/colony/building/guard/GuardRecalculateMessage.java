package com.minecolonies.coremod.network.messages.server.colony.building.guard;


public class GuardRecalculateMessage extends AbstractBuildingServerMessage<AbstractBuildingGuards>
{
    public GuardRecalculateMessage()
    {
    }

    @Override
    protected void toBytesOverride(final PacketBuffer buf)
    {

    }

    @Override
    protected void fromBytesOverride(final PacketBuffer buf)
    {

    }

    public GuardRecalculateMessage(final IBuildingView building)
    {
        super(building);
    }

    @Override
    protected void onExecute(
      final NetworkEvent.Context ctxIn, final boolean isLogicalServer, final IColony colony, final AbstractBuildingGuards building)
    {
        building.calculateMobs();
    }
}
