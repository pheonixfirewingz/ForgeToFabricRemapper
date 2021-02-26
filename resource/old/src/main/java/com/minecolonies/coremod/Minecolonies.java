package com.minecolonies.coremod;

import com.minecolonies.apiimp.CommonMinecoloniesAPIImpl;
import com.minecolonies.coremod.util.ModIdentifier;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.*;
import net.minecraft.item.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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