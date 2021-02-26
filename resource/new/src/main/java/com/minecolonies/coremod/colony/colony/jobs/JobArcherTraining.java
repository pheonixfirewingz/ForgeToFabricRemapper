package com.minecolonies.coremod.colony.colony.jobs;


/**
 * The Archers's Training Job class
 */
public class JobArcherTraining extends AbstractJob<EntityAIArcherTraining, JobArcherTraining>
{
    /**
     * Initialize citizen data.
     *
     * @param entity the citizen data.
     */
    public JobArcherTraining(final ICitizenData entity)
    {
        super(entity);
    }

    @Override
    public JobEntry getJobRegistryEntry()
    {
        return ModJobs.archer;
    }

    @Override
    public String getName()
    {
        return "com.minecolonies.coremod.job.archertraining";
    }

    @Override
    public String getExperienceTag()
    {
        return JobRanger.DESC;
    }

    @Override
    public BipedModelType getModel()
    {
        return BipedModelType.ARCHER_GUARD;
    }

    @Override
    public EntityAIArcherTraining generateAI()
    {
        return new EntityAIArcherTraining(this);
    }
}
