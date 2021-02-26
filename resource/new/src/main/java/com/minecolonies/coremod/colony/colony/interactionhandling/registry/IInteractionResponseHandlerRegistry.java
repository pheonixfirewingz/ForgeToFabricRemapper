package com.minecolonies.coremod.colony.colony.interactionhandling.registry;


public interface IInteractionResponseHandlerRegistry
{
    static IForgeRegistry<InteractionResponseHandlerEntry> getInstance()
    {
        return IMinecoloniesAPI.getInstance().getInteractionResponseHandlerRegistry();
    }
}
