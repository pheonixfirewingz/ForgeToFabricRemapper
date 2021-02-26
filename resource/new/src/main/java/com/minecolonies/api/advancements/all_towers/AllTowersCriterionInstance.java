package com.minecolonies.api.advancements.all_towers;


/**
 * All towers criterion instance.
 */
public class AllTowersCriterionInstance extends CriterionInstance
{
    public AllTowersCriterionInstance()
    {
        super(new ResourceLocation(Constants.MOD_ID, Constants.CRITERION_ALL_TOWERS), EntityPredicate.AndPredicate.ANY_AND);
    }
}
