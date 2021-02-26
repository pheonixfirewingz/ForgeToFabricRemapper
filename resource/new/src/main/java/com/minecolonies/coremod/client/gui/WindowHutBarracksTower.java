package com.minecolonies.coremod.client.gui;

import org.jetbrains.annotations.NotNull;

/**
 * Our building hut view
 */
public class WindowHutBarracksTower extends WindowHutGuardTower
{
    /**
     * Constructor for the window of the worker building.
     *
     * @param building class extending {@link AbstractBuildingGuards.View}.
     */
    public WindowHutBarracksTower(AbstractBuildingGuards.View building)
    {
        super(building);
    }

    @NotNull
    @Override
    public String getBuildingName()
    {
        return "com.minecolonies.coremod.gui.workerhuts.barrackstower";
    }
}
