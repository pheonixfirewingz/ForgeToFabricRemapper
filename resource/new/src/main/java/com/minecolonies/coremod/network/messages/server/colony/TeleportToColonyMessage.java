package com.minecolonies.coremod.network.messages.server.colony;

import org.jetbrains.annotations.Nullable;

/**
 * Message for trying to teleport to a friends colony.
 */
public class TeleportToColonyMessage extends AbstractColonyServerMessage
{
    public TeleportToColonyMessage()
    {
        super();
    }

    public TeleportToColonyMessage(final RegistryKey<World> dimensionId, final int colonyId)
    {
        super(dimensionId, colonyId);
    }

    @Nullable
    @Override
    public Action permissionNeeded()
    {
        return null;
    }

    @Override
    protected void onExecute(final NetworkEvent.Context ctxIn, final boolean isLogicalServer, final IColony colony)
    {
        if (ctxIn.getSender() == null)
        {
            return;
        }

        if (colony.getPermissions().getRank(ctxIn.getSender().getUniqueID()) != Rank.NEUTRAL)
        {
            TeleportHelper.colonyTeleport(ctxIn.getSender(), colony);
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
