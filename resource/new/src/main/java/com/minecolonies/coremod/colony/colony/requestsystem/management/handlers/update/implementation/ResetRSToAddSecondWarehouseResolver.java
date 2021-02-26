package com.minecolonies.coremod.colony.colony.requestsystem.management.handlers.update.implementation;

import org.jetbrains.annotations.NotNull;

/**
 * Update fix to ensure that the colony can use the new WarehouseConcreteRequestResolver. 
 */
public class ResetRSToAddSecondWarehouseResolver implements IUpdateStep
{
    @Override
    public int updatesToVersion()
    {
        return 6;
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
