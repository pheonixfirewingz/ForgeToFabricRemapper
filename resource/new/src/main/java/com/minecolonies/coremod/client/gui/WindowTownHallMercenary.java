package com.minecolonies.coremod.client.gui;

import org.jetbrains.annotations.NotNull;


/**
 * Gui for hiring mercenaries
 */
public class WindowTownHallMercenary extends Window implements ButtonHandler
{
    /**
     * The xml file for this gui
     */
    private static final String TOWNHALL_NAME_RESOURCE_SUFFIX = ":gui/townhall/windowtownhallmercenary.xml";

    /**
     * The client side colony data
     */
    private final IColonyView colony;

    /**
     * Constructor for a town hall rename entry window.
     *
     * @param c {@link ColonyView}
     */
    public WindowTownHallMercenary(final IColonyView c)
    {
        super(Constants.MOD_ID + TOWNHALL_NAME_RESOURCE_SUFFIX);
        this.colony = c;

        int amountOfMercenaries = colony.getCitizenCount();
        amountOfMercenaries = amountOfMercenaries / 10;
        amountOfMercenaries += 3;

        int startX = 160;
        final int startY = 40;

        for (int i = 0; i < amountOfMercenaries; i++)
        {
            final Image newImage = new Image();
            newImage.setImage("minecolonies:textures/entity_icon/citizenmale3.png");
            newImage.setSize(10, 10);
            newImage.setPosition(startX, startY);
            this.addChild(newImage);

            startX += 15;
        }
    }

    @Override
    public void onButtonClicked(@NotNull final Button button)
    {
        if (button.getID().equals(BUTTON_DONE))
        {
            colony.usedMercenaries();
            Network.getNetwork().sendToServer(new HireMercenaryMessage(colony));
            Minecraft.getInstance().player.playSound(SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.0f);
        }

        this.close();
    }
}
