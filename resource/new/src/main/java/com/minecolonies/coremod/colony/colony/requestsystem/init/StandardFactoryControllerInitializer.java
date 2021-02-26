package com.minecolonies.coremod.colony.colony.requestsystem.init;


/**
 * Initializer for the {@link StandardFactoryControllerInitializer}
 */
public final class StandardFactoryControllerInitializer
{

    /**
     * Private constructor to hide the implicit public one.
     */
    private StandardFactoryControllerInitializer()
    {
    }

    public static void onPreInit()
    {
        StandardFactoryController.getInstance().registerNewFactory(new StandardTokenFactory());
        StandardFactoryController.getInstance().registerNewFactory(new InitializedTokenFactory());
        StandardFactoryController.getInstance().registerNewFactory(new RandomSeededTokenFactory());
        StandardFactoryController.getInstance().registerNewFactory(new StaticLocation.Factory());
        StandardFactoryController.getInstance().registerNewFactory(new EntityLocation.Factory());
        StandardFactoryController.getInstance().registerNewFactory(new StandardRequestFactories.ItemStackRequestFactory());
        StandardFactoryController.getInstance().registerNewFactory(new StandardRequestFactories.ItemStackListRequestFactory());
        StandardFactoryController.getInstance().registerNewFactory(new StandardRequestFactories.ItemTagRequestFactory());
        StandardFactoryController.getInstance().registerNewFactory(new StandardRequestFactories.DeliveryRequestFactory());
        StandardFactoryController.getInstance().registerNewFactory(new StandardRequestFactories.PickupRequestFactory());
        StandardFactoryController.getInstance().registerNewFactory(new StandardRequestFactories.ToolRequestFactory());
        StandardFactoryController.getInstance().registerNewFactory(new StandardRequestFactories.FoodRequestFactory());
        StandardFactoryController.getInstance().registerNewFactory(new StandardRequestFactories.SmeltableOreRequestFactory());
        StandardFactoryController.getInstance().registerNewFactory(new StandardRequestFactories.BurnableRequestFactory());
        StandardFactoryController.getInstance().registerNewFactory(new BuildingBasedRequesterFactory());
        StandardFactoryController.getInstance().registerNewFactory(new DeliveryRequestResolverFactory());
        StandardFactoryController.getInstance().registerNewFactory(new PickupRequestResolverFactory());
        StandardFactoryController.getInstance().registerNewFactory(new WarehouseRequestResolverFactory());
        StandardFactoryController.getInstance().registerNewFactory(new WarehouseConcreteRequestResolverFactory());
        StandardFactoryController.getInstance().registerNewFactory(new PrivateWorkerCraftingRequestResolverFactory());
        StandardFactoryController.getInstance().registerNewFactory(new PublicWorkerCraftingRequestResolverFactory());
        StandardFactoryController.getInstance().registerNewFactory(new PrivateWorkerCraftingProductionResolverFactory());
        StandardFactoryController.getInstance().registerNewFactory(new PublicWorkerCraftingProductionResolverFactory());
        StandardFactoryController.getInstance().registerNewFactory(new BuildingRequestResolverFactory());
        StandardFactoryController.getInstance().registerNewFactory(new StandardPlayerRequestResolverFactory());
        StandardFactoryController.getInstance().registerNewFactory(new StandardRetryingRequestResolverFactory());
        StandardFactoryController.getInstance().registerNewFactory(new RecipeStorageFactory());
        StandardFactoryController.getInstance().registerNewFactory(new ItemStorageFactory());
        StandardFactoryController.getInstance().registerNewFactory(new GlobalResearchFactory());
        StandardFactoryController.getInstance().registerNewFactory(new LocalResearchFactory());
        StandardFactoryController.getInstance().registerNewFactory(new IntegerFactory());
        StandardFactoryController.getInstance().registerNewFactory(new TypeTokenFactory());
        StandardFactoryController.getInstance().registerNewFactory(new StandardRequestIdentitiesDataStore.Factory());
        StandardFactoryController.getInstance().registerNewFactory(new StandardRequestResolversIdentitiesDataStore.Factory());
        StandardFactoryController.getInstance().registerNewFactory(new StandardProviderRequestResolverAssignmentDataStore.Factory());
        StandardFactoryController.getInstance().registerNewFactory(new StandardRequestResolverRequestAssignmentDataStore.Factory());
        StandardFactoryController.getInstance().registerNewFactory(new StandardRequestableTypeRequestResolverAssignmentDataStore.Factory());
        StandardFactoryController.getInstance().registerNewFactory(new StandardRequestSystemBuildingDataStore.Factory());
        StandardFactoryController.getInstance().registerNewFactory(new StandardRequestSystemDeliveryManJobDataStore.Factory());
        StandardFactoryController.getInstance().registerNewFactory(new StandardRequestSystemCrafterJobDataStore.Factory());
        StandardFactoryController.getInstance().registerNewFactory(new StandardDataStoreManager.Factory());
        StandardFactoryController.getInstance().registerNewFactory(new StandardRequestFactories.PublicCraftingRequestFactory());
        StandardFactoryController.getInstance().registerNewFactory(new StandardRequestFactories.PrivateCraftingRequestFactory());

        StandardFactoryController.getInstance().registerNewTypeOverrideHandler(new TypeTokenFactory.TypeTokenSubTypeOverrideHandler());

        StandardFactoryController.getInstance()
          .registerNewClassRenaming("com.minecolonies.coremod.colony.requestsystem.resolvers.PlayerRequestResolver",
            "com.minecolonies.coremod.colony.requestsystem.resolvers.StandardPlayerRequestResolver");
    }
}