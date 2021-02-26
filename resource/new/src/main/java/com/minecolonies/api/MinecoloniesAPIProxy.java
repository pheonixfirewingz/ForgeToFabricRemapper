package com.minecolonies.api;


public final class MinecoloniesAPIProxy implements IMinecoloniesAPI
{
    private static final MinecoloniesAPIProxy ourInstance = new MinecoloniesAPIProxy();

    private IMinecoloniesAPI apiInstance;

    public static MinecoloniesAPIProxy getInstance()
    {
        return ourInstance;
    }

    private MinecoloniesAPIProxy()
    {
    }

    public void setApiInstance(final IMinecoloniesAPI apiInstance)
    {
        this.apiInstance = apiInstance;
    }

    @Override
    public IColonyManager getColonyManager()
    {
        return apiInstance.getColonyManager();
    }

    @Override
    public ICitizenDataManager getCitizenDataManager()
    {
        return apiInstance.getCitizenDataManager();
    }

    @Override
    public IMobAIRegistry getMobAIRegistry()
    {
        return apiInstance.getMobAIRegistry();
    }

    @Override
    public IPathNavigateRegistry getPathNavigateRegistry()
    {
        return apiInstance.getPathNavigateRegistry();
    }

    @Override
    public IBuildingDataManager getBuildingDataManager()
    {
        return apiInstance.getBuildingDataManager();
    }

    @Override
    public IForgeRegistry<BuildingEntry> getBuildingRegistry()
    {
        return apiInstance.getBuildingRegistry();
    }

    @Override
    public IJobDataManager getJobDataManager()
    {
        return apiInstance.getJobDataManager();
    }

    @Override
    public IForgeRegistry<JobEntry> getJobRegistry()
    {
        return apiInstance.getJobRegistry();
    }

    @Override
    public IForgeRegistry<InteractionResponseHandlerEntry> getInteractionResponseHandlerRegistry()
    {
        return apiInstance.getInteractionResponseHandlerRegistry();
    }

    @Override
    public IGuardTypeDataManager getGuardTypeDataManager()
    {
        return apiInstance.getGuardTypeDataManager();
    }

    @Override
    public IForgeRegistry<GuardType> getGuardTypeRegistry()
    {
        return apiInstance.getGuardTypeRegistry();
    }

    @Override
    public IModelTypeRegistry getModelTypeRegistry()
    {
        return apiInstance.getModelTypeRegistry();
    }

    @Override
    public Configuration getConfig()
    {
        return apiInstance.getConfig();
    }

    @Override
    public IFurnaceRecipes getFurnaceRecipes()
    {
        return apiInstance.getFurnaceRecipes();
    }

    @Override
    public IInteractionResponseHandlerDataManager getInteractionResponseHandlerDataManager()
    {
        return apiInstance.getInteractionResponseHandlerDataManager();
    }

    @Override
    public IGlobalResearchTree getGlobalResearchTree()
    {
        return apiInstance.getGlobalResearchTree();
    }

    @Override
    public IForgeRegistry<ColonyEventTypeRegistryEntry> getColonyEventRegistry()
    {
        return apiInstance.getColonyEventRegistry();
    }

    @Override
    public IForgeRegistry<ColonyEventDescriptionTypeRegistryEntry> getColonyEventDescriptionRegistry()
    {
        return apiInstance.getColonyEventDescriptionRegistry();
    }

    @Override
    public IForgeRegistry<RecipeTypeEntry> getRecipeTypeRegistry()
    {
        return apiInstance.getRecipeTypeRegistry();
    }
}
