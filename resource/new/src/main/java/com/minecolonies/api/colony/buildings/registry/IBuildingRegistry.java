package com.minecolonies.api.colony.buildings.registry;


public interface IBuildingRegistry
{

    static IForgeRegistry<BuildingEntry> getInstance()
    {
        return IMinecoloniesAPI.getInstance().getBuildingRegistry();
    }
}
