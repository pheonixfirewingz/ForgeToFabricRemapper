package com.minecolonies.api.advancements.place_supply;


/**
 * A default instance for the "place_supply" trigger, as the conditions are handled in events
 */
public class PlaceSupplyCriterionInstance extends CriterionInstance
{
    public PlaceSupplyCriterionInstance()
    {
        super(new ResourceLocation(Constants.MOD_ID, Constants.CRITERION_SUPPLY_PLACED), EntityPredicate.AndPredicate.ANY_AND);
    }
}
