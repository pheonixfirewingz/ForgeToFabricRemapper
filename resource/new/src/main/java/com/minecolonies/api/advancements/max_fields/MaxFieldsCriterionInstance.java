package com.minecolonies.api.advancements.max_fields;


/**
 * All towers criterion instance.
 */
public class MaxFieldsCriterionInstance extends CriterionInstance
{
    public MaxFieldsCriterionInstance()
    {
        super(new ResourceLocation(Constants.MOD_ID, Constants.CRITERION_MAX_FIELDS), EntityPredicate.AndPredicate.ANY_AND);
    }
}
