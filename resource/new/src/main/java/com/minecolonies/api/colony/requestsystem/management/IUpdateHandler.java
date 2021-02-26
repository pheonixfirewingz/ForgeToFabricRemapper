package com.minecolonies.api.colony.requestsystem.management;


public interface IUpdateHandler
{
    IRequestManager getManager();

    void handleUpdate(final UpdateType type);

    int getCurrentVersion();
}
