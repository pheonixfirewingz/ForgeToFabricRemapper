package com.minecolonies.coremod.colony.colony.jobs;

import org.jetbrains.annotations.NotNull;

/**
 * Class of the planter job.
 */
public class JobPlanter extends AbstractJobCrafter<EntityAIWorkPlanter, JobPlanter>
{
    /**
     * Instantiates the job for the plantation.
     *
     * @param entity the citizen who becomes a planter
     */
    public JobPlanter(final ICitizenData entity)
    {
        super(entity);
    }

    @Override
    public JobEntry getJobRegistryEntry()
    {
        return ModJobs.planter;
    }

    @NotNull
    @Override
    public String getName()
    {
        return "com.minecolonies.coremod.job.planter";
    }

    @NotNull
    @Override
    public BipedModelType getModel()
    {
        return BipedModelType.PLANTER;
    }

    /**
     * Generate your AI class to register.
     *
     * @return your personal AI instance.
     */
    @NotNull
    @Override
    public EntityAIWorkPlanter generateAI()
    {
        return new EntityAIWorkPlanter(this);
    }

    @Override
    public boolean ignoresDamage(@NotNull final DamageSource damageSource)
    {
        return damageSource == DamageSource.CACTUS;
    }
}