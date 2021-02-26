package com.minecolonies.coremod.entity.ai.citizen.dyer;

import org.jetbrains.annotations.NotNull;

/**
 * Crafts dye related things.
 */
public class EntityAIWorkDyer extends AbstractEntityAIRequestSmelter<JobDyer, BuildingDyer>
{
    /**
     * Initialize the dyer.
     *
     * @param dyer the job he has.
     */
    public EntityAIWorkDyer(@NotNull final JobDyer dyer)
    {
        super(dyer);
    }

    @Override
    public Class<BuildingDyer> getExpectedBuildingClass()
    {
        return BuildingDyer.class;
    }
}
