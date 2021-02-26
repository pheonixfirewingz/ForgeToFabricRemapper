package com.minecolonies.coremod.client.gui;

import org.jetbrains.annotations.NotNull;


/**
 * Townhallgui for deleting the owned colony
 */
public class WindowTownHallColonyDelete extends AbstractWindowSkeleton
{
    private static final String BUTTON_CONFIRM = "confirm";

    public WindowTownHallColonyDelete()
    {
        super(MOD_ID + TOWNHALL_COLONY_DELETE_GUI);
    }

    @Override
    public void onButtonClicked(@NotNull final Button button)
    {
        if (button.getID().equals(BUTTON_CONFIRM))
        {
            // Delete colony
            Network.getNetwork().sendToServer(new ColonyDeleteOwnMessage());
        }

        close();
    }
}
