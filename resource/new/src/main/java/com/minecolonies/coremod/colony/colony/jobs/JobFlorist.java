package com.minecolonies.coremod.colony.colony.jobs;

import org.jetbrains.annotations.NotNull;

public class JobFlorist extends AbstractJob<EntityAIWorkFlorist, JobFlorist>
{
    /**
     * Initialize citizen data.
     *
     * @param entity the citizen data.
     */
    public JobFlorist(final ICitizenData entity)
    {
        super(entity);
    }

    @Override
    public JobEntry getJobRegistryEntry()
    {
        return ModJobs.florist;
    }

    @Override
    public String getName()
    {
        return "com.minecolonies.coremod.job.florist";
    }

    @NotNull
    @Override
    public BipedModelType getModel()
    {
        return BipedModelType.COMPOSTER;
    }

    @Override
    public EntityAIWorkFlorist generateAI()
    {
        return new EntityAIWorkFlorist(this);
    }
}
