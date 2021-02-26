package com.minecolonies.coremod.colony.colony.requestsystem.management.handlers.update;

import org.jetbrains.annotations.NotNull;

public interface IUpdateStep
{
    int updatesToVersion();

    default void update(@NotNull final UpdateType type, @NotNull final IStandardRequestManager manager)
    {
        this.update(manager);
    }

    default void update(@NotNull final IStandardRequestManager manager)
    {

    }
}
