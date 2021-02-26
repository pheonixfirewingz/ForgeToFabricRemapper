package com.minecolonies.api.colony.jobs.registry;


public interface IJobRegistry
{
    static IForgeRegistry<JobEntry> getInstance()
    {
        return IMinecoloniesAPI.getInstance().getJobRegistry();
    }
}
