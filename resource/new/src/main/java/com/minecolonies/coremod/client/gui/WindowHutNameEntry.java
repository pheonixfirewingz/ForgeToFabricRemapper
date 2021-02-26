package com.minecolonies.coremod.client.gui;

import org.jetbrains.annotations.NotNull;


/**
 * Window for a hut name entry.
 */
public class WindowHutNameEntry extends Window implements ButtonHandler
{
    /**
     * The max length of the name.
     */
    private static final int MAX_NAME_LENGTH = 15;

    /**
     * Resource suffix of GUI xml file.
     */
    private static final String HUT_NAME_RESOURCE_SUFFIX = ":gui/windowhutnameentry.xml";

    /**
     * The building associated to the GUI.
     */
    private final IBuildingView building;

    /**
     * Constructor for a hut rename entry window.
     *
     * @param b {@link AbstractBuilding}
     */
    public WindowHutNameEntry(final IBuildingView b)
    {
        super(Constants.MOD_ID + HUT_NAME_RESOURCE_SUFFIX);
        this.building = b;
    }

    @Override
    public void onOpened()
    {
        findPaneOfTypeByID(INPUT_NAME, TextField.class).setText(building.getCustomName());
    }

    @Override
    public void onButtonClicked(@NotNull final Button button)
    {
        if (button.getID().equals(BUTTON_DONE))
        {
            String name = findPaneOfTypeByID(INPUT_NAME, TextField.class).getText();

            if (name.length() > MAX_NAME_LENGTH)
            {
                name = name.substring(0, MAX_NAME_LENGTH);
                LanguageHandler.sendPlayerMessage(Minecraft.getInstance().player, "com.minecolonies.coremod.gui.name.toolong", name);
            }

            building.setCustomName(name);
        }
        else if (!button.getID().equals(BUTTON_CANCEL))
        {
            return;
        }

        if (building != null)
        {
            building.openGui(false);
        }
    }
}
