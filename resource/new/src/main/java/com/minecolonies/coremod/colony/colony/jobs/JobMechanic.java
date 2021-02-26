package com.minecolonies.coremod.colony.colony.jobs;

import org.jetbrains.annotations.NotNull;

/**
 * Class of the Mechanic job.
 */
public class JobMechanic extends AbstractJobCrafter<EntityAIWorkMechanic, JobMechanic>
{
    /**
     * Instantiates the job for the Mechanic.
     *
     * @param entity the citizen who becomes a Sawmill
     */
    public JobMechanic(final ICitizenData entity)
    {
        super(entity);
    }

    @Override
    public JobEntry getJobRegistryEntry()
    {
        return ModJobs.mechanic;
    }

    @NotNull
    @Override
    public String getName()
    {
        return "com.minecolonies.coremod.job.mechanic";
    }

    /**
     * Generate your AI class to register.
     *
     * @return your personal AI instance.
     */
    @NotNull
    @Override
    public EntityAIWorkMechanic generateAI()
    {
        return new EntityAIWorkMechanic(this);
    }

    @NotNull
    @Override
    public IModelType getModel()
    {
        return BipedModelType.MECHANIST;
    }
}
