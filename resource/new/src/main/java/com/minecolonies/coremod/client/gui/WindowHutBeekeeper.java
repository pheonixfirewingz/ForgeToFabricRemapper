package com.minecolonies.coremod.client.gui;


/**
 * Window for the beekeeper hut.
 */
public class WindowHutBeekeeper extends AbstractWindowWorkerBuilding<BuildingBeekeeper.View>
{
    private static final String BUTTON_HARVEST_HONEYCOMB = "harvestHoneycomb";

    /**
     * Id of the button to give tool
     */
    private static final String BUTTON_GIVE_TOOL = "giveTool";

    /**
     * Button for toggling honeycomb harvesting.
     */
    private final Button buttonHarvestHoneycombs;

    /**
     * Constructor for the window of the worker building.
     *
     * @param building {@link BuildingBeekeeper.View}.
     */
    public WindowHutBeekeeper(final BuildingBeekeeper.View building)
    {
        super(building, Constants.MOD_ID + ":gui/windowhutbeekeeper.xml");
        registerButton(BUTTON_HARVEST_HONEYCOMB, this::harvestHoneycombClicked);

        buttonHarvestHoneycombs = findPaneOfTypeByID(BUTTON_HARVEST_HONEYCOMB, Button.class);

        if (building.isHarvestHoneycombs())
        {
            buttonHarvestHoneycombs.setText(LanguageHandler.format(TranslationConstants.COM_MINECOLONIES_COREMOD_GUI_BEEKEEPER_COLLECT));
        }
        else
        {
            buttonHarvestHoneycombs.setText(LanguageHandler.format(TranslationConstants.COM_MINECOLONIES_COREMOD_GUI_BEEKEEPER_NOTCOLLECT));
        }

        registerButton(BUTTON_GIVE_TOOL, this::givePlayerScepter);
    }

    /**
     * Send message to player to add scepter to his inventory.
     */
    private void givePlayerScepter()
    {
        Network.getNetwork().sendToServer(new BeekeeperScepterMessage(building));
    }

    private void harvestHoneycombClicked()
    {
        if (buttonHarvestHoneycombs.getTextAsString().equals(LanguageHandler.format(TranslationConstants.COM_MINECOLONIES_COREMOD_GUI_BEEKEEPER_COLLECT)))
        {
            buttonHarvestHoneycombs.setText(LanguageHandler.format(TranslationConstants.COM_MINECOLONIES_COREMOD_GUI_BEEKEEPER_NOTCOLLECT));
            building.setHarvestHoneycombs(false);
        }
        else
        {
            buttonHarvestHoneycombs.setText(LanguageHandler.format(TranslationConstants.COM_MINECOLONIES_COREMOD_GUI_BEEKEEPER_COLLECT));
            building.setHarvestHoneycombs(true);
        }
    }

    /**
     * Returns the name of a building.
     *
     * @return Name of a building.
     */
    @Override
    public String getBuildingName()
    {
        return "com.minecolonies.coremod.gui.workerhuts.beekeeperHut";
    }
}
