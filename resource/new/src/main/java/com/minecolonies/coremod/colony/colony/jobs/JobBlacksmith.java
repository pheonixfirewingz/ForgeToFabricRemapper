package com.minecolonies.coremod.colony.colony.jobs;

import org.jetbrains.annotations.NotNull;

/**
 * Class of the Blacksmith job.
 */
public class JobBlacksmith extends AbstractJobCrafter<EntityAIWorkBlacksmith, JobBlacksmith>
{
    /**
     * Instantiates the job for the Blacksmith.
     *
     * @param entity the citizen who becomes a Sawmill
     */
    public JobBlacksmith(final ICitizenData entity)
    {
        super(entity);
    }

    @Override
    public JobEntry getJobRegistryEntry()
    {
        return ModJobs.blacksmith;
    }

    @NotNull
    @Override
    public String getName()
    {
        return "com.minecolonies.coremod.job.Blacksmith";
    }

    @NotNull
    @Override
    public BipedModelType getModel()
    {
        return BipedModelType.BLACKSMITH;
    }

    /**
     * Generate your AI class to register.
     *
     * @return your personal AI instance.
     */
    @Override
    public EntityAIWorkBlacksmith generateAI()
    {
        return new EntityAIWorkBlacksmith(this);
    }
}
