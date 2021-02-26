package com.minecolonies.coremod.colony.colony.jobs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Class of the placeholder job. Used if a certain building doesn't have a job yet.
 */
public class JobPlaceholder extends AbstractJob<AbstractAISkeleton<JobPlaceholder>, JobPlaceholder>
{
    /**
     * Instantiates the placeholder job.
     *
     * @param entity the entity.
     */
    public JobPlaceholder(final ICitizenData entity)
    {
        super(entity);
    }

    @Override
    public JobEntry getJobRegistryEntry()
    {
        return ModJobs.placeHolder;
    }

    @NotNull
    @Override
    public String getName()
    {
        return "com.minecolonies.coremod.job.Placeholder";
    }

    /**
     * Generate your AI class to register.
     *
     * @return your personal AI instance.
     */
    @Nullable
    @Override
    public AbstractAISkeleton<JobPlaceholder> generateAI()
    {
        return null;
    }
}
