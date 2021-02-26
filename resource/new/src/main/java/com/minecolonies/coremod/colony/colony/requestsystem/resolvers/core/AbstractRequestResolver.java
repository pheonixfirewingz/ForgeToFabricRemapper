package com.minecolonies.coremod.colony.colony.requestsystem.resolvers.core;

import org.jetbrains.annotations.NotNull;


/**
 * ----------------------- Not Documented Object ---------------------
 */
public abstract class AbstractRequestResolver<R extends IRequestable> implements IRequestResolver<R>
{
    @NotNull
    private final ILocation location;

    @NotNull
    private final IToken<?> token;

    public AbstractRequestResolver(@NotNull final ILocation location, @NotNull final IToken<?> token)
    {
        this.location = location;
        this.token = token;
    }

    @Override
    public IToken<?> getId()
    {
        return token;
    }

    @NotNull
    @Override
    public ILocation getLocation()
    {
        return location;
    }

    @NotNull
    @Override
    public IFormattableTextComponent getRequesterDisplayName(@NotNull final IRequestManager manager, @NotNull final IRequest<?> request)
    {
        return new StringTextComponent("Request System");
    }

    @Override
    public int getPriority()
    {
        return CONST_DEFAULT_RESOLVER_PRIORITY;
    }
}
