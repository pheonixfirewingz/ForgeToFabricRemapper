package com.minecolonies.coremod.colony.colony.jobs;

import org.jetbrains.annotations.NotNull;

/**
 * Job class of the pupil.
 */
public class JobPupil extends AbstractJob<EntityAIWorkPupil, JobPupil>
{
    /**
     * Public constructor of the pupil job.
     *
     * @param entity the entity to assign to the job.
     */
    public JobPupil(final ICitizenData entity)
    {
        super(entity);
    }

    @Override
    public JobEntry getJobRegistryEntry()
    {
        return ModJobs.pupil;
    }

    @NotNull
    @Override
    public String getName()
    {
        return "com.minecolonies.coremod.job.pupil";
    }

    @NotNull
    @Override
    public BipedModelType getModel()
    {
        return BipedModelType.CHILD;
    }

    /**
     * Override to add Job-specific AI tasks to the given EntityAITask list.
     */
    @NotNull
    @Override
    public EntityAIWorkPupil generateAI()
    {
        return new EntityAIWorkPupil(this);
    }
}
