package com.minecolonies.coremod.datalistener;

import com.google.gson.*;


import java.util.Map;


/**
 * Loader for Json based crafter specific recipes
 */
public class CrafterRecipeListener extends JsonReloadListener
{
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    /**
     * Set up the core loading, with the directory in the datapack that contains this data
     * Directory is: <namespace>/crafterrecipes/<path>
     */
    public CrafterRecipeListener()
    {
        super(GSON, "crafterrecipes");
    }

    @Override
    protected void apply(final Map<ResourceLocation, JsonElement> object, final IResourceManager resourceManagerIn, final IProfiler profilerIn)
    {
        Log.getLogger().info("Beginning load of custom recipes for colony workers");

        CustomRecipeManager recipeManager = CustomRecipeManager.getInstance();
        for(Map.Entry<ResourceLocation, JsonElement> entry : object.entrySet())
        {
            ResourceLocation key = entry.getKey();

            JsonObject recipeJson = entry.getValue().getAsJsonObject();

            if(recipeJson.has(RECIPE_TYPE_PROP) && recipeJson.get(RECIPE_TYPE_PROP).getAsString().equals(RECIPE_TYPE_RECIPE))
            {
                recipeManager.addRecipe(recipeJson, key);
            }

            if(recipeJson.has(RECIPE_TYPE_PROP) && recipeJson.get(RECIPE_TYPE_PROP).getAsString().equals(RECIPE_TYPE_RECIPE_MULT_OUT)) 
            {
                recipeManager.addRecipe(recipeJson, key);
            }

            if(recipeJson.has(RECIPE_TYPE_PROP) && recipeJson.get(RECIPE_TYPE_PROP).getAsString().equals(RECIPE_TYPE_REMOVE)) 
            {
                recipeManager.removeRecipe(recipeJson, key);
            }
        } 

        final int totalRecipes = recipeManager.getAllRecipes().values().stream().mapToInt(x -> x.size()).sum();
        Log.getLogger().info("Loaded " + totalRecipes + " recipes for " + recipeManager.getAllRecipes().size() + " crafters");
    }
}
