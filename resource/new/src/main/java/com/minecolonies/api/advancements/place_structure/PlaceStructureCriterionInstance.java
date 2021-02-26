package com.minecolonies.api.advancements.place_structure;


/**
 * The test instance to check the "hut_name" or "structure_name" for the "place_structure" trigger
 */
public class PlaceStructureCriterionInstance extends CriterionInstance
{
    private String        hutName;
    private StructureName structureName;

    public PlaceStructureCriterionInstance()
    {
        super(new ResourceLocation(Constants.MOD_ID, Constants.CRITERION_STRUCTURE_PLACED), EntityPredicate.AndPredicate.ANY_AND);
    }

    /**
     * Construct the check with a single condition
     * @param hutName the hut that has to be placed to succeed
     */
    public PlaceStructureCriterionInstance(final String hutName)
    {
        super(new ResourceLocation(Constants.MOD_ID, Constants.CRITERION_STRUCTURE_PLACED), EntityPredicate.AndPredicate.ANY_AND);

        this.hutName = hutName;
    }

    /**
     * Construct the check with a single condition
     * @param structureName the structure that has to be placed to succeed
     */
    public PlaceStructureCriterionInstance(final StructureName structureName)
    {
        super(new ResourceLocation(Constants.MOD_ID, Constants.CRITERION_STRUCTURE_PLACED), EntityPredicate.AndPredicate.ANY_AND);

        this.structureName = structureName;
    }

    /**
     * Performs the check for the conditions
     * @param structureName the id of the structure that was just placed
     * @return whether the check succeeded
     */
    public boolean test(final StructureName structureName)
    {
        if (this.hutName != null)
        {
            return this.hutName.equalsIgnoreCase(structureName.getHutName());
        }

        if (this.structureName != null)
        {
            return this.structureName.equals(structureName);
        }

        return true;
    }
}
