package com.minecolonies.coremod.entity.ai.citizen.glassblower;

import org.jetbrains.annotations.NotNull;

/**
 * Crafts glass relates things, crafts and smelts.
 */
public class EntityAIWorkGlassblower extends AbstractEntityAIRequestSmelter<JobGlassblower, BuildingGlassblower>
{
    /**
     * Initialize the glass blower AI.
     *
     * @param glassBlower the job he has.
     */
    public EntityAIWorkGlassblower(@NotNull final JobGlassblower glassBlower)
    {
        super(glassBlower);
    }

    @Override
    public Class<BuildingGlassblower> getExpectedBuildingClass()
    {
        return BuildingGlassblower.class;
    }
}
