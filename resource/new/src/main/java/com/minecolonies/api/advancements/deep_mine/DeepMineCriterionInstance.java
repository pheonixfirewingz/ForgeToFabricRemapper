package com.minecolonies.api.advancements.deep_mine;


/**
 * All towers criterion instance.
 */
public class DeepMineCriterionInstance extends CriterionInstance
{
    public DeepMineCriterionInstance()
    {
        super(new ResourceLocation(Constants.MOD_ID, Constants.CRITERION_DEEP_MINE), EntityPredicate.AndPredicate.ANY_AND);
    }
}
