package com.minecolonies.coremod.network.messages.server.colony;

import org.jetbrains.annotations.Nullable;

/**
 * Message for deleting an owned colony
 */
public class ColonyDeleteOwnMessage implements IMessage
{
    @Override
    public void toBytes(final PacketBuffer buf)
    {

    }

    @Override
    public void fromBytes(final PacketBuffer buf)
    {

    }

    @Nullable
    @Override
    public LogicalSide getExecutionSide()
    {
        return LogicalSide.SERVER;
    }

    @Override
    public void onExecute(final NetworkEvent.Context ctxIn, final boolean isLogicalServer)
    {
        final ServerPlayerEntity player = ctxIn.getSender();
        if (player == null)
        {
            return;
        }

        final IColony colony = IColonyManager.getInstance().getIColonyByOwner(player.world, player);
        if (colony != null)
        {
            IColonyManager.getInstance().deleteColonyByDimension(colony.getID(), false, colony.getDimension());
            LanguageHandler.sendPlayerMessage(player, "com.minecolonies.coremod.gui.colony.delete.success");
        }
        else
        {
            LanguageHandler.sendPlayerMessage(
              player,
              "com.minecolonies.command.nocolony", this.getClass().getSimpleName()
            );
        }
    }
}