package com.minecolonies.coremod.client.gui;

import org.jetbrains.annotations.NotNull;


/**
 * Window for the tavern
 */
public class WindowHutTavern extends AbstractWindowBuilding<TavernBuildingModule.View>
{
    /**
     * Id of the hire/fire button in the GUI.
     */
    private static final String BUTTON_ASSIGN = "assign";

    /**
     * Id of the hire/fire button in the GUI.
     */
    private static final String BUTTON_REMOVE = "remove";

    /**
     * Suffix describing the window xml.
     */
    private static final String HOME_BUILDING_RESOURCE_SUFFIX = ":gui/windowhuthome.xml";

    /**
     * Id to identify the list of the citizen in the view.
     */
    private static final String LIST_CITIZEN = "assignedCitizen";

    /**
     * The building the view is relates to.
     */
    private final LivingBuildingView home;

    /**
     * The list of citizen assigned to this hut.
     */
    private ScrollingList citizen;

    /**
     * Creates the Window object.
     *
     * @param building View of the home building.
     */
    public WindowHutTavern(final TavernBuildingModule.View building)
    {
        super(building, Constants.MOD_ID + HOME_BUILDING_RESOURCE_SUFFIX);

        super.registerButton(BUTTON_ASSIGN, this::assignClicked);
        super.registerButton(BUTTON_REMOVE, this::removeClicked);
        super.registerButton(BUTTON_RECALL, this::recallClicked);

        this.home = building;
    }

    /**
     * On recall clicked.
     */
    private void recallClicked()
    {
        Network.getNetwork().sendToServer(new RecallCitizenHutMessage(building));
    }

    @Override
    public void onOpened()
    {
        super.onOpened();
        citizen = findPaneOfTypeByID(LIST_CITIZEN, ScrollingList.class);
        citizen.setDataProvider(new ScrollingList.DataProvider()
        {
            @Override
            public int getElementCount()
            {
                return home.getResidents().size();
            }

            @Override
            public void updateElement(final int index, @NotNull final Pane rowPane)
            {
                final ICitizenDataView citizenDataView = home.getColony().getCitizen(home.getResidents().get(index));
                if (citizenDataView != null)
                {
                    rowPane.findPaneOfTypeByID("name", Text.class).setText(citizenDataView.getName());
                    rowPane.findPaneOfTypeByID(BUTTON_REMOVE, Button.class).setEnabled(building.getColony().isManualHousing());
                }
            }
        });

        refreshView();
    }

    /**
     * Refresh the view.
     */
    private void refreshView()
    {
        final Button buttonAssign = findPaneOfTypeByID(BUTTON_ASSIGN, Button.class);

        final int sparePlaces = 4 - building.getResidents().size();
        buttonAssign.setText(LanguageHandler.format(COM_MINECOLONIES_COREMOD_GUI_HOME_ASSIGN, sparePlaces));
        buttonAssign.setEnabled(sparePlaces > 0 && building.getColony().isManualHousing());

        citizen.refreshElementPanes();
    }

    /**
     * Action when an assign button is clicked.
     */
    private void assignClicked()
    {
        if (building.getColony().isManualHousing())
        {
            if (building.getBuildingLevel() == 0)
            {
                LanguageHandler.sendPlayerMessage(Minecraft.getInstance().player, COM_MINECOLONIES_COREMOD_GUI_WORKERHUTS_LEVEL_0);
                return;
            }

            if (building.getResidents().size() < 4)
            {
                @NotNull final WindowAssignCitizen window = new WindowAssignCitizen(building.getColony(), building.getPosition());
                window.open();
            }
        }
    }

    /**
     * Action when the remove button is clicked.
     *
     * @param button the clicked button.
     */
    private void removeClicked(@NotNull final Button button)
    {
        if (building.getColony().isManualHousing())
        {
            final int row = citizen.getListElementIndexByPane(button);
            final int citizenid = home.getResidents().get(row);
            home.removeResident(row);
            Network.getNetwork().sendToServer(new AssignUnassignMessage(building, false, citizenid));
            refreshView();
        }
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
        return "com.minecolonies.coremod.gui.workerhuts.tavern";
    }
}
