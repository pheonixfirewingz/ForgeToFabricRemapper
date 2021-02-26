package com.minecolonies.coremod.colony.colony.jobs;

import org.jetbrains.annotations.NotNull;

/**
 * Class of the Stonemason job.
 */
public class JobStonemason extends AbstractJobCrafter<EntityAIWorkStonemason, JobStonemason>
{
    /**
     * Instantiates the job for the Stonemason.
     *
     * @param entity the citizen who becomes a Sawmill
     */
    public JobStonemason(final ICitizenData entity)
    {
        super(entity);
    }

    @Override
    public JobEntry getJobRegistryEntry()
    {
        return ModJobs.stoneMason;
    }

    @NotNull
    @Override
    public String getName()
    {
        return "com.minecolonies.coremod.job.Stonemason";
    }

    /**
     * Generate your AI class to register.
     *
     * @return your personal AI instance.
     */
    @NotNull
    @Override
    public EntityAIWorkStonemason generateAI()
    {
        return new EntityAIWorkStonemason(this);
    }
}
