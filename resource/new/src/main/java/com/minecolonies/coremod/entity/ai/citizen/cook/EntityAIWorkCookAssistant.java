package com.minecolonies.coremod.entity.ai.citizen.cook;

import org.jetbrains.annotations.NotNull;


/**
 * Crafts food related things.
 */
public class EntityAIWorkCookAssistant extends AbstractEntityAIRequestSmelter<JobCookAssistant, BuildingCook>
{
    /**
     * Initialize the Cook Assistant.
     *
     * @param cookAssistant the job he has.
     */
    public EntityAIWorkCookAssistant(@NotNull final JobCookAssistant cookAssistant)
    {
        super(cookAssistant);
    }

    @Override
    public Class<BuildingCook> getExpectedBuildingClass()
    {
        return BuildingCook.class;
    }

    /**
     * Main method to decide on what to do.
     *
     * @return the next state to go to.
     */
    protected IAIState decide()
    {
        IAIState nextState = super.decide();
        // Only mark is cooking if the current recipe is a furnace recipe, to keep the cook from messing with the furnaces
        if (job.hasTask() && !getOwnBuilding().getIsCooking() && currentRecipeStorage != null && currentRecipeStorage.getIntermediate() == Blocks.FURNACE)
        {
            getOwnBuilding().setIsCooking(true);
        }
        if(!job.hasTask() && getOwnBuilding().getIsCooking())
        {
            getOwnBuilding().setIsCooking(false);
        }
        return nextState;
    }
}
