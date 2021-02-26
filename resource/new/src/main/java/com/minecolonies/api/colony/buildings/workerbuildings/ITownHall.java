package com.minecolonies.api.colony.buildings.workerbuildings;


public interface ITownHall extends IBuilding
{
    /**
     * Add a colony permission event to the colony. Reduce the list by one if bigger than a treshhold.
     *
     * @param event the event to add.
     */
    void addPermissionEvent(PermissionEvent event);
}
