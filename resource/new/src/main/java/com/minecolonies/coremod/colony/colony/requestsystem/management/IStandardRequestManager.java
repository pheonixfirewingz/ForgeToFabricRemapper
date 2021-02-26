package com.minecolonies.coremod.colony.colony.requestsystem.management;

import org.jetbrains.annotations.NotNull;

/**
 * Describes the {@link StandardRequestManager} data access. Is only used for internal handling.
 */
public interface IStandardRequestManager extends IRequestManager
{

    @NotNull
    IRequestIdentitiesDataStore getRequestIdentitiesDataStore();

    @NotNull
    IRequestResolverIdentitiesDataStore getRequestResolverIdentitiesDataStore();

    @NotNull
    IProviderResolverAssignmentDataStore getProviderResolverAssignmentDataStore();

    @NotNull
    IRequestResolverRequestAssignmentDataStore getRequestResolverRequestAssignmentDataStore();

    @NotNull
    IRequestableTypeRequestResolverAssignmentDataStore getRequestableTypeRequestResolverAssignmentDataStore();

    IProviderHandler getProviderHandler();

    IRequestHandler getRequestHandler();

    IResolverHandler getResolverHandler();

    ITokenHandler getTokenHandler();

    IUpdateHandler getUpdateHandler();

    int getCurrentVersion();

    void setCurrentVersion(int currentVersion);
}
