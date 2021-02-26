package com.minecolonies.api.colony.requestsystem.management;


public interface ITokenHandler
{
    IRequestManager getManager();

    /**
     * Generates a new Token for the request system.
     *
     * @return The new token.
     */
    IToken<?> generateNewToken();
}
