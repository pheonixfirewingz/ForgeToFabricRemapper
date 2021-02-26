package com.minecolonies.coremod.colony.colony.requestsystem.management.handlers.update.implementation;

import org.jetbrains.annotations.NotNull;

/**
 * Update fix to register the new Pickup-Resolver for deliverymen.
 */
public class ResetRSToAddDelivermanPickups implements IUpdateStep
{
    @Override
    public int updatesToVersion()
    {
        return 3;
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
