package com.minecolonies.api.colony.buildings.modules;


/**
 * Interface describing core building stats.
 * The first core stats module that is found in the building will define the values.
 */
public interface IDefinesCoreBuildingStatsModule extends IBuildingModule
{
    /**
     * Get the max number of inhabitants this module allows.
     * @return the modules max number of assigned citizens.
     */
    IStat<Integer> getMaxInhabitants();
}
