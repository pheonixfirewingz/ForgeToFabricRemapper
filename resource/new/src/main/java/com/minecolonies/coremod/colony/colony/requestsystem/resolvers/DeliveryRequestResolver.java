package com.minecolonies.coremod.colony.colony.requestsystem.resolvers;

import com.google.common.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

/**
 * Resolves deliveries
 */
public class DeliveryRequestResolver extends DeliverymenRequestResolver<Delivery>
{
    public DeliveryRequestResolver(
      @NotNull final ILocation location,
      @NotNull final IToken<?> token)
    {
        super(location, token);
    }

    @Override
    public boolean canResolveRequest(@NotNull final IRequestManager manager, final IRequest<? extends Delivery> requestToCheck)
    {
        final IWareHouse wareHouse = manager.getColony().getBuildingManager().getClosestWarehouseInColony(requestToCheck.getRequest().getStart().getInDimensionLocation());
        if (wareHouse == null || !wareHouse.getID().equals(getLocation().getInDimensionLocation()))
        {
            return false;
        }

        return super.canResolveRequest(manager, requestToCheck);
    }

    @Override
    public TypeToken<? extends Delivery> getRequestType()
    {
        return TypeConstants.DELIVERY;
    }

}
