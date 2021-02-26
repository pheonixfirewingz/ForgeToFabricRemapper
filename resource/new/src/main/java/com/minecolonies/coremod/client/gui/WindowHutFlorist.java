package com.minecolonies.coremod.client.gui;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;


/**
 * Florist window class. Specifies the extras the florist has for its list.
 */
public class WindowHutFlorist extends AbstractHutFilterableLists
{
    /**
     * View containing the list.
     */
    private static final String PAGE_ITEMS_VIEW = "flowers";

    /**
     * The resource string.
     */
    private static final String RESOURCE_STRING = ":gui/windowhutflorist.xml";

    /**
     * The max level the building doesn't let filtering yet.
     */
    private static final int MAX_LEVEL_BEFORE_SORTING = 3;

    /**
     * The building of the florist (Client side representation).
     */
    private final BuildingFlorist.View ownBuilding;

    /**
     * Constructor for the window of the worker building.
     *
     * @param building class extending
     */
    public WindowHutFlorist(final BuildingFlorist.View building)
    {
        super(building, Constants.MOD_ID + RESOURCE_STRING);

        final ViewFilterableList window = new ViewFilterableList(findPaneOfTypeByID(PAGE_ITEMS_VIEW, View.class),
          this,
          building,
          LanguageHandler.format(FLORIST_FLOWER_DESC),
          PAGE_ITEMS_VIEW,
          true);
        views.put(PAGE_ITEMS_VIEW, window);
        this.ownBuilding = building;
    }

    @Override
    public void onButtonClicked(@NotNull final Button button)
    {
        if (Objects.equals(button.getID(), BUTTON_SWITCH))
        {
            if (ownBuilding.getBuildingLevel() <= 1)
            {
                LanguageHandler.sendPlayerMessage(Minecraft.getInstance().player, TOO_LOW_LEVEL_TO_FILTER_FLORIST);
                return;
            }

            if (ownBuilding.getBuildingLevel() <= MAX_LEVEL_BEFORE_SORTING && button.getTextAsString().equals(ON) && building.getSize(PAGE_ITEMS_VIEW) >= 1)
            {
                LanguageHandler.sendPlayerMessage(Minecraft.getInstance().player, TOO_MANY_FILTERED_BELOW_LVL4_FLORIST);
                return;
            }

            if (ownBuilding.getBuildingLevel() < MAX_BUILDING_LEVEL && button.getTextAsString().equals(ON) && building.getSize(PAGE_ITEMS_VIEW) >= MAX_BUILDING_LEVEL)
            {
                LanguageHandler.sendPlayerMessage(Minecraft.getInstance().player, TOO_MANY_FILTERED_FLORIST);
                return;
            }
        }

        super.onButtonClicked(button);
    }

    @Override
    public List<? extends ItemStorage> getBlockList(final Predicate<ItemStack> filterPredicate, final String id)
    {
        return BuildingFlorist.getPlantablesForBuildingLevel(building.getBuildingLevel());
    }

    @Override
    public String getBuildingName()
    {
        return FLORIST_BUILDING_NAME;
    }
}
