package com.minecolonies.coremod.colony.colony.requestsystem.resolvers;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * ----------------------- Not Documented Object ---------------------
 */
public class WarehouseConcreteRequestResolver extends AbstractWarehouseRequestResolver
{
    public WarehouseConcreteRequestResolver(
      @NotNull final ILocation location,
      @NotNull final IToken<?> token)
    {
        super(location, token);
    }

    @Override
    protected boolean internalCanResolve(final List<TileEntityWareHouse> wareHouses, final IRequest<? extends IDeliverable> requestToCheck)
    {
        final IDeliverable deliverable = requestToCheck.getRequest();

        if(deliverable instanceof IConcreteDeliverable)
        {
            boolean ignoreNBT = false;
            if (deliverable instanceof Stack && !((Stack) requestToCheck.getRequest()).matchNBT())
            {
                ignoreNBT = true;
            }
            for(final ItemStack possible : ((IConcreteDeliverable) deliverable).getRequestedItems())
            {
                for (final TileEntityWareHouse wareHouse : wareHouses)
                {
                    if (wareHouse.hasMatchingItemStackInWarehouse(possible, requestToCheck.getRequest().getMinimumCount(), ignoreNBT))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
