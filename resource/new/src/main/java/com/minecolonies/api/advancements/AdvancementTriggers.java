package com.minecolonies.api.advancements;


/**
 * The collection of advancement triggers for minecolonies.
 * Each trigger may correspond to multiple advancements.
 */
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
