package com.minecolonies.coremod.colony.colony.requestsystem.init;

import org.jetbrains.annotations.NotNull;

{

    public static void onPostInit()
    {
        reconfigureLogging();

        Log.getLogger().warn("Register mappings");
        RequestMappingHandler.registerRequestableTypeMapping(Stack.class, StandardRequests.ItemStackRequest.class);
        RequestMappingHandler.registerRequestableTypeMapping(Burnable.class, StandardRequests.BurnableRequest.class);
        RequestMappingHandler.registerRequestableTypeMapping(Delivery.class, StandardRequests.DeliveryRequest.class);
        RequestMappingHandler.registerRequestableTypeMapping(Pickup.class, StandardRequests.PickupRequest.class);
        RequestMappingHandler.registerRequestableTypeMapping(Food.class, StandardRequests.FoodRequest.class);
        RequestMappingHandler.registerRequestableTypeMapping(Tool.class, StandardRequests.ToolRequest.class);
        RequestMappingHandler.registerRequestableTypeMapping(SmeltableOre.class, StandardRequests.SmeltAbleOreRequest.class);
        RequestMappingHandler.registerRequestableTypeMapping(StackList.class, StandardRequests.ItemStackListRequest.class);
        RequestMappingHandler.registerRequestableTypeMapping(PublicCrafting.class, StandardRequests.PublicCraftingRequest.class);
        RequestMappingHandler.registerRequestableTypeMapping(PrivateCrafting.class, StandardRequests.PrivateCraftingRequest.class);
        RequestMappingHandler.registerRequestableTypeMapping(Tag.class, StandardRequests.ItemTagRequest.class);
    }

    private static void reconfigureLogging()
    {
        // TODO: move to config listener
        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final Configuration config = ctx.getConfiguration();
        final LoggerConfig loggerConfig = getLoggerConfiguration(config, String.format("%s.requestsystem", Constants.MOD_ID));
        loggerConfig.addFilter(LevelRangeFilter.createFilter(Level.FATAL,
          MineColonies.getConfig().getServer().enableDebugLogging.get() ? Level.DEBUG : Level.INFO,
          Filter.Result.NEUTRAL,
          Filter.Result.DENY));

        ctx.updateLoggers();

        LogManager.getLogger(String.format("%s.requestsystem", Constants.MOD_ID)).warn(String.format("Updated logging config. RS Debug logging enabled: %s",
          MineColonies.getConfig().getServer().enableDebugLogging.get()));
    }

    private static LoggerConfig getLoggerConfiguration(@NotNull final Configuration configuration, @NotNull final String loggerName)
    {
        final LoggerConfig lc = configuration.getLoggerConfig(loggerName);
        if (lc.getName().equals(loggerName))
        {
            return lc;
        }
        else
        {
            final LoggerConfig nlc = new LoggerConfig(loggerName, lc.getLevel(), lc.isAdditive());
            nlc.setParent(lc);
            configuration.addLogger(loggerName, nlc);
            configuration.getLoggerContext().updateLoggers();

            return nlc;
        }
    }
}
