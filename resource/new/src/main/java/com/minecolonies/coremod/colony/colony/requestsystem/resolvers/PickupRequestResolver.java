package com.minecolonies.coremod.colony.colony.requestsystem.resolvers;

import com.google.common.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

/**
 * Resolver that handles pickup requests. Pickups don't have much logic inherently, because the only important information are the requester and the priority. These resolvers are
 * supposed to be provided by deliverymen.
 * <p>
 * Currently, this resolver will iterate through all available deliverymen and find the one with the least amount of open requests, followed by the one that is nearest.
 * <p>
 * There is a tiny bit of (known) code-smell in here, since this resolver should either be global or specific to a hut. Currently, it is a hut-specific resolver that acts as if it
 * were global. The performance impact is negligible though.
 */
public class PickupRequestResolver extends DeliverymenRequestResolver<Pickup>
{
    public PickupRequestResolver(
      @NotNull final ILocation location,
      @NotNull final IToken<?> token)
    {
        super(location, token);
    }

    @Override
    public boolean canResolveRequest(@NotNull final IRequestManager manager, final IRequest<? extends Pickup> requestToCheck)
    {
        final IWareHouse wareHouse = manager.getColony().getBuildingManager().getClosestWarehouseInColony(requestToCheck.getRequester().getLocation().getInDimensionLocation());
        if (wareHouse == null || !wareHouse.getID().equals(getLocation().getInDimensionLocation()))
        {
            return false;
        }

        return super.canResolveRequest(manager, requestToCheck);
    }


    @Override
    public TypeToken<? extends Pickup> getRequestType()
    {
        return TypeConstants.PICKUP;
    }
}
