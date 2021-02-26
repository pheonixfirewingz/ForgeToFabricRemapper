package com.minecolonies.coremod.colony.colony.jobs;


import org.jetbrains.annotations.NotNull;

/**
 * Class of the CookAssistant job.
 */
public class JobCookAssistant extends AbstractJobCrafter<EntityAIWorkCookAssistant, JobCookAssistant>
{
    /**
     * Instantiates the job for the CookAssistant.
     *
     * @param entity the citizen who becomes a CookAssistant.
     */
    public JobCookAssistant(final ICitizenData entity)
    {
        super(entity);
    }

    @Override
    public JobEntry getJobRegistryEntry()
    {
        return ModJobs.cookassistant;
    }

    @NotNull
    @Override
    public String getName()
    {
        return "com.minecolonies.coremod.job.cookassistant";
    }

    @NotNull
    @Override
    public IModelType getModel()
    {
        return BipedModelType.COOK;
    }

    /**
     * Generate your AI class to register.
     *
     * @return your personal AI instance.
     */
    @NotNull
    @Override
    public EntityAIWorkCookAssistant generateAI()
    {
        return new EntityAIWorkCookAssistant(this);
    }
}
