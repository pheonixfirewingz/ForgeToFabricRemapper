package com.minecolonies.coremod.colony.colony.jobs;

import org.jetbrains.annotations.NotNull;

/**
 * Class of the Sawmill job.
 */
public class JobSawmill extends AbstractJobCrafter<EntityAIWorkSawmill, JobSawmill>
{
    /**
     * Instantiates the job for the Sawmill.
     *
     * @param entity the citizen who becomes a Sawmill
     */
    public JobSawmill(final ICitizenData entity)
    {
        super(entity);
    }

    @Override
    public JobEntry getJobRegistryEntry()
    {
        return ModJobs.sawmill;
    }

    @NotNull
    @Override
    public String getName()
    {
        return "com.minecolonies.coremod.job.Sawmill";
    }

    /**
     * Generate your AI class to register.
     *
     * @return your personal AI instance.
     */
    @NotNull
    @Override
    public EntityAIWorkSawmill generateAI()
    {
        return new EntityAIWorkSawmill(this);
    }
}
