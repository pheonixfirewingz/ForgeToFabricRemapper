package com.minecolonies.coremod.colony.colony.requestsystem.requesters;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface IBuildingBasedRequester extends IRequester
{
    /**
     * Get the building.
     *
     * @param manager the manager.
     * @param token   the token.
     * @return the IRequester or empty.
     */
    Optional<IRequester> getBuilding(@NotNull final IRequestManager manager, @NotNull final IToken<?> token);
}
