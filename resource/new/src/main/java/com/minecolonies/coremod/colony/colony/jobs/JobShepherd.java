package com.minecolonies.coremod.colony.colony.jobs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The Shepherd job
 */
public class JobShepherd extends AbstractJob<EntityAIWorkShepherd, JobShepherd>
{

    /**
     * Instantiates the placeholder job.
     *
     * @param entity the entity.
     */
    public JobShepherd(final ICitizenData entity)
    {
        super(entity);
    }

    @Override
    public JobEntry getJobRegistryEntry()
    {
        return ModJobs.shepherd;
    }

    @NotNull
    @Override
    public String getName()
    {
        return "com.minecolonies.coremod.job.Shepherd";
    }

    /**
     * Generate your AI class to register.
     *
     * @return your personal AI instance.
     */
    @Nullable
    @Override
    public EntityAIWorkShepherd generateAI()
    {
        return new EntityAIWorkShepherd(this);
    }

    /**
     * Get the RenderBipedCitizen.Model to use when the Citizen performs this job role.
     *
     * @return Model of the citizen.
     */
    @NotNull
    @Override
    public BipedModelType getModel()
    {
        return BipedModelType.SHEEP_FARMER;
    }
}
