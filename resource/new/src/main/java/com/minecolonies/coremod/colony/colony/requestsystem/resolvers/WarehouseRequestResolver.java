package com.minecolonies.coremod.colony.colony.requestsystem.resolvers;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * ----------------------- Not Documented Object ---------------------
 */
public class WarehouseRequestResolver extends AbstractWarehouseRequestResolver
{
    public WarehouseRequestResolver(
      @NotNull final ILocation location,
      @NotNull final IToken<?> token)
    {
        super(location, token);
    }

    @Override
    protected boolean internalCanResolve(final List<TileEntityWareHouse> wareHouses, final IRequest<? extends IDeliverable> requestToCheck)
    {
        if(requestToCheck.getRequest() instanceof IConcreteDeliverable)
        {
            return false; 
        }

        for (final TileEntityWareHouse wareHouse : wareHouses)
        {
            if (wareHouse.hasMatchingItemStackInWarehouse(itemStack -> requestToCheck.getRequest().matches(itemStack), requestToCheck.getRequest().getMinimumCount()))
            {
                return true;
            }
        }
        return false;
    }
}
