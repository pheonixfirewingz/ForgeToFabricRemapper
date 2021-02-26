package com.minecolonies.coremod.network.messages.client;

import org.jetbrains.annotations.Nullable;

/**
 * Asks the client to stop its music
 */
public class StopMusicMessage implements IMessage
{
    public StopMusicMessage()
    {
        super();
    }

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
        return LogicalSide.CLIENT;
    }

    @Override
    public void onExecute(final NetworkEvent.Context ctxIn, final boolean isLogicalServer)
    {
        Minecraft.getInstance().getSoundHandler().stop();
        Minecraft.getInstance().getMusicTicker().stop();
    }
}
