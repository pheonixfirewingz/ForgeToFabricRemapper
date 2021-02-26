package com.minecolonies.coremod;



public class Minecolonies implements ModInitializer
{

    public static Logger LOGGER = LogManager.getLogger();

    public static final String MOD_ID = "minecolonies";
    public static final String MOD_NAME = "Minecolonies";

    public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(new ModIdentifier(MOD_ID),
            () -> new ItemStack(Blocks.BARREL));

    @Override
    public void onInitialize()
    {
        log("Initializing");
        MinecoloniesBlocks.registerBlocks();
        MinecoloniesItems.registerItems();
        MinecoloniesWorld.registerWorldGen();
    }

    public static void log(String message)
    {
        LOGGER.log(Level.INFO, "[" + MOD_NAME + "] " + message);
    }

    public static void logWarn(String message)
    {
        LOGGER.log(Level.WARN, "[" + MOD_NAME + "] " + message);
    }

    public static void logError(String message)
    {
        LOGGER.log(Level.ERROR, "[" + MOD_NAME + "] " + message);
    }

}
