package com.minecolonies.coremod.colony.colony.jobs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The Cowboy job
 */
public class JobCowboy extends AbstractJob<EntityAIWorkCowboy, JobCowboy>
{
    /**
     * Instantiates the placeholder job.
     *
     * @param entity the entity.
     */
    public JobCowboy(final ICitizenData entity)
    {
        super(entity);
    }

    @Override
    public JobEntry getJobRegistryEntry()
    {
        return ModJobs.cowboy;
    }

    @NotNull
    @Override
    public String getName()
    {
        return "com.minecolonies.coremod.job.Cowboy";
    }

    /**
     * Generate your AI class to register.
     *
     * @return your personal AI instance.
     */
    @Nullable
    @Override
    public EntityAIWorkCowboy generateAI()
    {
        return new EntityAIWorkCowboy(this);
    }

    /**
     * Get the RenderBipedCitizen.Model to use when the Citizen performs this job role.
     *
     * @return Model of the citizen.
     */
    @NotNull
    @Override
    public BipedModelType getModel()
    {
        return BipedModelType.COW_FARMER;
    }
}
