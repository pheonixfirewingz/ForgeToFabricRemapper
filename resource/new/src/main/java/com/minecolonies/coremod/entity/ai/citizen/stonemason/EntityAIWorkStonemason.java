package com.minecolonies.coremod.entity.ai.citizen.stonemason;

import org.jetbrains.annotations.NotNull;

/**
 * Crafts stone related block when needed.
 */
public class EntityAIWorkStonemason extends AbstractEntityAICrafting<JobStonemason, BuildingStonemason>
{
    /**
     * Initialize the Stonemason and add all his tasks.
     *
     * @param stonemason the job he has.
     */
    public EntityAIWorkStonemason(@NotNull final JobStonemason stonemason)
    {
        super(stonemason);
    }

    @Override
    public Class<BuildingStonemason> getExpectedBuildingClass()
    {
        return BuildingStonemason.class;
    }
}
