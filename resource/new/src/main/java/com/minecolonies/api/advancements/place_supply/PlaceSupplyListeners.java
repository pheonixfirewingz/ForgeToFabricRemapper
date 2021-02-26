package com.minecolonies.api.advancements.place_supply;


/**
 * A default listener, as there are no conditions
 */
public class PlaceSupplyListeners extends CriterionListeners<PlaceSupplyCriterionInstance>
{
    public PlaceSupplyListeners(final PlayerAdvancements playerAdvancements)
    {
        super(playerAdvancements);
    }

    public void trigger()
    {
        trigger(instance -> true);
    }
}
