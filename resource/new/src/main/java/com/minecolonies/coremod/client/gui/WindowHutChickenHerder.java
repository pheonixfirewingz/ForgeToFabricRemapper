package com.minecolonies.coremod.client.gui;

import org.jetbrains.annotations.NotNull;

/**
 * Window for the chicken herder hut.
 */
public class WindowHutChickenHerder extends AbstractWindowHerderBuilding<BuildingChickenHerder.View>
{
    /**
     * Constructor for the window of the chicken herder.
     *
     * @param building {@link BuildingChickenHerder.View}.
     */
    public WindowHutChickenHerder(final BuildingChickenHerder.View building)
    {
        super(building, Constants.MOD_ID + ":gui/windowhutchickenherder.xml");
    }

    /**
     * Returns the name of a building.
     *
     * @return Name of a building.
     */
    @NotNull
    @Override
    public String getBuildingName()
    {
        return "com.minecolonies.coremod.gui.workerhuts.chickenHerderHut";
    }
}
