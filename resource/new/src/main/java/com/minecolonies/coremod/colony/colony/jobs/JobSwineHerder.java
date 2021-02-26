package com.minecolonies.coremod.colony.colony.jobs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The SwineHerder job
 */
public class JobSwineHerder extends AbstractJob<EntityAIWorkSwineHerder, JobSwineHerder>
{
    /**
     * Instantiates the placeholder job.
     *
     * @param entity the entity.
     */
    public JobSwineHerder(final ICitizenData entity)
    {
        super(entity);
    }

    @Override
    public JobEntry getJobRegistryEntry()
    {
        return ModJobs.swineHerder;
    }

    @NotNull
    @Override
    public String getName()
    {
        return "com.minecolonies.coremod.job.SwineHerder";
    }

    /**
     * Generate your AI class to register.
     *
     * @return your personal AI instance.
     */
    @Nullable
    @Override
    public EntityAIWorkSwineHerder generateAI()
    {
        return new EntityAIWorkSwineHerder(this);
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
        return BipedModelType.PIG_FARMER;
    }

    @Override
    public int getDiseaseModifier()
    {
        return 2;
    }
}
