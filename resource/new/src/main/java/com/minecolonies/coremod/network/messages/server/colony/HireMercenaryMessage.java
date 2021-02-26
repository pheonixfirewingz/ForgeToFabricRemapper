package com.minecolonies.coremod.network.messages.server.colony;

import org.jetbrains.annotations.Nullable;

/**
 * The message sent when activating mercenaries
 */
public class HireMercenaryMessage extends AbstractColonyServerMessage
{
    public HireMercenaryMessage()
    {
    }

    public HireMercenaryMessage(final IColony colony)
    {
        super(colony);
    }

    @Nullable
    @Override
    public Action permissionNeeded()
    {
        return super.permissionNeeded();
    }

    @Override
    protected void onExecute(final NetworkEvent.Context ctxIn, final boolean isLogicalServer, final IColony colony)
    {
        final PlayerEntity player = ctxIn.getSender();
        if (player == null)
        {
            return;
        }

        EntityMercenary.spawnMercenariesInColony(colony);
        colony.getWorld()
          .playSound(player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ENTITY_ILLUSIONER_CAST_SPELL, null, 1.0f, 1.0f, true);
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
