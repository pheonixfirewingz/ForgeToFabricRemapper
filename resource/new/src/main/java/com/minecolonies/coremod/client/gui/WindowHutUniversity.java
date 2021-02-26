package com.minecolonies.coremod.client.gui;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


/**
 * Window for the university.
 */
public class WindowHutUniversity extends AbstractWindowWorkerBuilding<BuildingUniversity.View>
{
    /**
     * The list of branches of the tree.
     */
    private final List<String> branches = new ArrayList<>();

    /**
     * Constructor for the window of the lumberjack.
     *
     * @param building {@link BuildingUniversity.View}.
     */
    public WindowHutUniversity(final BuildingUniversity.View building)
    {
        super(building, Constants.MOD_ID + RESOURCE_STRING);

        final View view = this.findPaneOfTypeByID(BRANCH_VIEW_ID, View.class);
        int offset = 0;
        for (final String branch : IGlobalResearchTree.getInstance().getBranches())
        {
            final ButtonImage button = new ButtonImage();
            button.setImage(new ResourceLocation(Constants.MOD_ID, MEDIUM_SIZED_BUTTON_RES));
            button.setText(branch);
            button.setSize(BUTTON_LENGTH, BUTTON_HEIGHT);
            button.setTextRenderBox(BUTTON_LENGTH, BUTTON_HEIGHT);
            button.setTextAlignment(Alignment.MIDDLE);
            button.setColors(SLIGHTLY_BLUE);
            button.setPosition(x + INITITAL_X_OFFSET, y + offset + INITITAL_Y_OFFSET);
            view.addChild(button);
            branches.add(branch);

            offset += button.getHeight() + BUTTON_PADDING;
        }
    }

    @Override
    public void onButtonClicked(@NotNull final Button button)
    {
        super.onButtonClicked(button);
        final String label = button.getTextAsString();

        if (branches.contains(label))
        {
            new WindowResearchTree(label, building, this).open();
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
        return COM_MINECOLONIES_COREMOD_GUI_UNIVERSITY;
    }
}