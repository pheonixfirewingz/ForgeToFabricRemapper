package com.minecolonies.coremod.client.gui;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


/**
 * Window for the crafter hut.
 */
public class WindowHutCrafter extends AbstractWindowWorkerBuilding<AbstractBuildingCrafter.View>
{
    /**
     * Resource suffix of the GUI.
     */
    private static final String HUT_RESOURCE_SUFFIX = ":gui/windowhutcrafter.xml";

    /**
     * Id of the the task list inside the GUI.
     */
    private static final String LIST_TASKS = "tasks";

    /**
     * The name of the specific one.
     */
    private final String name;

    /**
     * Constructor for the window of the crafter.
     *
     * @param building {@link AbstractBuildingCrafter.View}.
     */
    public WindowHutCrafter(final AbstractBuildingCrafter.View building, final String name)
    {
        super(building, Constants.MOD_ID + HUT_RESOURCE_SUFFIX);
        this.name = name;
    }

    @Override
    public void onOpened()
    {
        super.onOpened();
        final List<IToken<?>> tasks = new ArrayList<>();

        for (final int citizenId : building.getWorkerId())
        {
            ICitizenDataView citizen = building.getColony().getCitizen(citizenId);
            if (citizen != null && citizen.getJobView() instanceof CrafterJobView)
            {
                tasks.addAll(((CrafterJobView) citizen.getJobView()).getDataStore().getQueue());
            }
        }

        final ScrollingList deliveryList = findPaneOfTypeByID(LIST_TASKS, ScrollingList.class);
        deliveryList.setDataProvider(new ScrollingList.DataProvider()
        {
            @Override
            public int getElementCount()
            {
                tasks.removeIf(token -> building.getColony().getRequestManager().getRequestForToken(token) == null);
                return tasks.size();
            }

            @Override
            public void updateElement(final int index, @NotNull final Pane rowPane)
            {
                final IRequest<?> request = building.getColony().getRequestManager().getRequestForToken(tasks.get(index));

                final IRequest<?> parent = building.getColony().getRequestManager().getRequestForToken(request.getParent());

                if (parent != null)
                {
                    rowPane.findPaneOfTypeByID(REQUESTER, Text.class)
                      .setText(request.getRequester().getRequesterDisplayName(building.getColony().getRequestManager(), request).getString() + " ->");
                    rowPane.findPaneOfTypeByID(PARENT, Text.class)
                      .setText(parent.getRequester().getRequesterDisplayName(building.getColony().getRequestManager(), parent));
                }
                else
                {
                    rowPane.findPaneOfTypeByID(REQUESTER, Text.class)
                      .setText(request.getRequester().getRequesterDisplayName(building.getColony().getRequestManager(), request));
                    rowPane.findPaneOfTypeByID(PARENT, Text.class).clearText();
                }

                rowPane.findPaneOfTypeByID(REQUEST_SHORT_DETAIL, Text.class)
                  .setText(request.getShortDisplayString().getString().replace("§f", ""));

                if (request.getRequest() instanceof IDeliverymanRequestable)
                {
                    rowPane.findPaneOfTypeByID(REQUEST_PRIORITY, Text.class)
                      .setText(
                        LanguageHandler.format(COM_MINECOLONIES_COREMOD_ENTITY_DELIVERYMAN_PRIORITY) + ((IDeliverymanRequestable) (request.getRequest())).getPriority());
                }

                final Image logo = rowPane.findPaneOfTypeByID(DELIVERY_IMAGE, Image.class);
                logo.setImage(request.getDisplayIcon());
            }
        });
    }

    @NotNull
    @Override
    public String getBuildingName()
    {
        return "com.minecolonies.coremod.gui.workerhuts." + name;
    }
}
