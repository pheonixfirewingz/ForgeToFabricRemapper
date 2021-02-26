package com.minecolonies.coremod.colony.colony.requestsystem.management.handlers.update.implementation;

import org.jetbrains.annotations.NotNull;

public class InitialUpdate implements IUpdateStep
{
    @Override
    public int updatesToVersion()
    {
        return 0;
    }

    @Override
    public void update(@NotNull final IStandardRequestManager manager)
    {
        //Noop
    }
}
