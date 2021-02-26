package com.minecolonies.coremod.colony.colony.requestsystem.management.handlers.update.implementation;

import org.jetbrains.annotations.NotNull;

/**
 * Changed delivery resolvers
 */
public class ResetRSForDeliveryResolverChange implements IUpdateStep
{
    @Override
    public int updatesToVersion()
    {
        return 8;
    }

    @Override
    public void update(@NotNull final UpdateType type, @NotNull final IStandardRequestManager manager)
    {
        if (type == UpdateType.DATA_LOAD)
        {
            manager.reset();
        }
    }
}
