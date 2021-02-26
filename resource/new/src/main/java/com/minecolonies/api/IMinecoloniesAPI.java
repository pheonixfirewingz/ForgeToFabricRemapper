package com.minecolonies.api;


public interface IMinecoloniesAPI
{

    static IMinecoloniesAPI getInstance()
    {
        return MinecoloniesAPIProxy.getInstance();
    }

    IColonyManager getColonyManager();

    ICitizenDataManager getCitizenDataManager();

    IMobAIRegistry getMobAIRegistry();

    IPathNavigateRegistry getPathNavigateRegistry();

    IBuildingDataManager getBuildingDataManager();

    IForgeRegistry<BuildingEntry> getBuildingRegistry();

    IJobDataManager getJobDataManager();

    IForgeRegistry<JobEntry> getJobRegistry();

    IForgeRegistry<InteractionResponseHandlerEntry> getInteractionResponseHandlerRegistry();

    IGuardTypeDataManager getGuardTypeDataManager();

    IForgeRegistry<GuardType> getGuardTypeRegistry();

    IModelTypeRegistry getModelTypeRegistry();

    Configuration getConfig();

    IFurnaceRecipes getFurnaceRecipes();

    IInteractionResponseHandlerDataManager getInteractionResponseHandlerDataManager();

    IGlobalResearchTree getGlobalResearchTree();

    IForgeRegistry<ColonyEventTypeRegistryEntry> getColonyEventRegistry();

    IForgeRegistry<ColonyEventDescriptionTypeRegistryEntry> getColonyEventDescriptionRegistry();

    IForgeRegistry<RecipeTypeEntry> getRecipeTypeRegistry();
}
