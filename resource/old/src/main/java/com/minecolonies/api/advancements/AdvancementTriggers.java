package com.minecolonies.api.advancements;

import com.minecolonies.api.advancements.all_towers.AllTowersTrigger;
import com.minecolonies.api.advancements.army_population.ArmyPopulationTrigger;
import com.minecolonies.api.advancements.building_add_recipe.BuildingAddRecipeTrigger;
import com.minecolonies.api.advancements.citizen_eat_food.CitizenEatFoodTrigger;
import com.minecolonies.api.advancements.click_gui_button.ClickGuiButtonTrigger;
import com.minecolonies.api.advancements.colony_population.ColonyPopulationTrigger;
import com.minecolonies.api.advancements.complete_build_request.CompleteBuildRequestTrigger;
import com.minecolonies.api.advancements.create_build_request.CreateBuildRequestTrigger;
import com.minecolonies.api.advancements.deep_mine.DeepMineTrigger;
import com.minecolonies.api.advancements.max_fields.MaxFieldsTrigger;
import com.minecolonies.api.advancements.open_gui_window.OpenGuiWindowTrigger;
import com.minecolonies.api.advancements.place_structure.PlaceStructureTrigger;
import com.minecolonies.api.advancements.place_supply.PlaceSupplyTrigger;
import net.minecraft.advancement.criterion.*;

/**
 * The collection of advancement triggers for minecolonies.
 * Each trigger may correspond to multiple advancements.
 */
public class AdvancementTriggers
{
    public static final PlaceSupplyTrigger          PLACE_SUPPLY           = new PlaceSupplyTrigger();
    public static final PlaceStructureTrigger       PLACE_STRUCTURE        = new PlaceStructureTrigger();
    public static final CreateBuildRequestTrigger   CREATE_BUILD_REQUEST   = new CreateBuildRequestTrigger();
    public static final OpenGuiWindowTrigger        OPEN_GUI_WINDOW        = new OpenGuiWindowTrigger();
    public static final ClickGuiButtonTrigger       CLICK_GUI_BUTTON       = new ClickGuiButtonTrigger();
    public static final CitizenEatFoodTrigger       CITIZEN_EAT_FOOD       = new CitizenEatFoodTrigger();
    public static final BuildingAddRecipeTrigger    BUILDING_ADD_RECIPE    = new BuildingAddRecipeTrigger();
    public static final CompleteBuildRequestTrigger COMPLETE_BUILD_REQUEST = new CompleteBuildRequestTrigger();
    public static final ColonyPopulationTrigger     COLONY_POPULATION      = new ColonyPopulationTrigger();
    public static final ArmyPopulationTrigger       ARMY_POPULATION        = new ArmyPopulationTrigger();
    public static final MaxFieldsTrigger            MAX_FIELDS             = new MaxFieldsTrigger();
    public static final DeepMineTrigger             DEEP_MINE              = new DeepMineTrigger();
    public static final AllTowersTrigger            ALL_TOWERS             = new AllTowersTrigger();

    /**
     * Registers all the triggers so they can be referenced in the advancement JSON
     */
    public static void preInit()
    {
        Criteria.register(PLACE_SUPPLY);
        Criteria.register(PLACE_STRUCTURE);
        Criteria.register(CREATE_BUILD_REQUEST);
        Criteria.register(OPEN_GUI_WINDOW);
        Criteria.register(CLICK_GUI_BUTTON);
        Criteria.register(CITIZEN_EAT_FOOD);
        Criteria.register(BUILDING_ADD_RECIPE);
        Criteria.register(COMPLETE_BUILD_REQUEST);
        Criteria.register(COLONY_POPULATION);
        Criteria.register(ARMY_POPULATION);
        Criteria.register(MAX_FIELDS);
        Criteria.register(DEEP_MINE);
        Criteria.register(ALL_TOWERS);
    }
}
