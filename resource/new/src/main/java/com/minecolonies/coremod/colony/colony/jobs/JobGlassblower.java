package com.minecolonies.coremod.colony.colony.jobs;

import org.jetbrains.annotations.NotNull;

/**
 * Class of the Glassblower job.
 */
public class JobGlassblower extends AbstractJobCrafter<EntityAIWorkGlassblower, JobGlassblower>
{
    /**
     * Instantiates the job for the Glassblower.
     *
     * @param entity the citizen who becomes a Glassblower.
     */
    public JobGlassblower(final ICitizenData entity)
    {
        super(entity);
    }

    @Override
    public JobEntry getJobRegistryEntry()
    {
        return ModJobs.glassblower;
    }

    @NotNull
    @Override
    public String getName()
    {
        return "com.minecolonies.coremod.job.glassblower";
    }

    @NotNull
    @Override
    public IModelType getModel()
    {
        return BipedModelType.GLASSBLOWER;
    }

    /**
     * Generate your AI class to register.
     *
     * @return your personal AI instance.
     */
    @NotNull
    @Override
    public EntityAIWorkGlassblower generateAI()
    {
        return new EntityAIWorkGlassblower(this);
    }
}
