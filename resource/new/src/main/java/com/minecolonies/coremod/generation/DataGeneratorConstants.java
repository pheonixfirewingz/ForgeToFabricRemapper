package com.minecolonies.coremod.generation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

{

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private static final String DATAPACK_DIR = "data/" + Constants.MOD_ID + "/";

    // DataPack Directories \\

    public static final String LOOT_TABLES_DIR = DATAPACK_DIR + "loot_tables/blocks";

    public static final String ASSETS_DIR = "assets/" + Constants.MOD_ID + "/";
}
