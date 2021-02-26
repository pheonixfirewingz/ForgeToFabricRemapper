package com.minecolonies.coremod.colony.colony.crafting;

import java.util.*;
import java.util.stream.Collectors;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;



/**
 * Manager class for tracking Custom recipes during load and use
 * This class is a singleton
 */
{
    /**
     * The internal static instance of the singleton
     */
    private static final CustomRecipeManager instance = new CustomRecipeManager();

    /**
     * The map of loaded recipes
     */
    private final HashMap<String, Map<ResourceLocation, CustomRecipe>> recipeMap = new HashMap<>();

    /**
     * The recipes that are marked for removal after loading all resource packs
     * This list will be processed on first access of the custom recipe list after load, and will be emptied.
     */
    private final List<ResourceLocation> removedRecipes = new ArrayList<>();

    private CustomRecipeManager()
    {
    }
        
    /**
     * Get the singleton instance of this class
     * @return
     */
    public static CustomRecipeManager getInstance()
    {
        return instance;
    }

    /**
     * Add recipe to manager.
     * @param recipeJson
     * @param recipeLocation
     */
    public void addRecipe(@NotNull final JsonObject recipeJson, @NotNull final ResourceLocation recipeLocation)
    {
        CustomRecipe recipe = CustomRecipe.parse(recipeJson);
        recipe.setRecipeId(recipeLocation);

        if(!recipeMap.containsKey(recipe.getCrafter()))
        {
            recipeMap.put(recipe.getCrafter(), new HashMap<>());
        }

        recipeMap.get(recipe.getCrafter()).put(recipeLocation, recipe);
    }

    /**
     * Remove recipe
     * @param recipeJson
     * @param recipeLocation
     */
    public void removeRecipe(@NotNull final JsonObject recipeJson, @NotNull final ResourceLocation recipeLocation)
    {
        if (recipeJson.has(RECIPE_TYPE_PROP) && recipeJson.get(RECIPE_TYPE_PROP).getAsString().equals(RECIPE_TYPE_REMOVE) && recipeJson.has(RECIPE_ID_TO_REMOVE_PROP))
        {
            ResourceLocation toRemove = new ResourceLocation(recipeJson.get(RECIPE_ID_TO_REMOVE_PROP).getAsString());
            if(!removedRecipes.contains(toRemove))
            {
                removedRecipes.add(toRemove);
            }
        }
    }

    /**
     * Get all of the custom recipes that apply to a particular crafter
     * @param crafter
     * @return
     */
    public Set<CustomRecipe> getRecipes(@NotNull final String crafter)
    {
        if(!removedRecipes.isEmpty())
        {
            for(ResourceLocation toRemove: removedRecipes)
            {
                final Optional<Map<ResourceLocation, CustomRecipe>> crafterMap = recipeMap.entrySet().stream().map(r -> r.getValue()).filter(r1 -> r1.containsKey(toRemove)).findFirst();
                if(crafterMap.isPresent())
                {
                    crafterMap.get().remove(toRemove);
                }
             }
            removedRecipes.clear();
        }

        if(recipeMap.containsKey(crafter))
        {
            return recipeMap.get(crafter).entrySet().stream().map(x -> x.getValue()).collect(Collectors.toSet());
        }
        return new HashSet<>();
    }

    /**
     * The complete list of custom recipes, by crafter. 
     */
    public Map<String, Map<ResourceLocation, CustomRecipe>> getAllRecipes()
    {
        return recipeMap;
    }
    
}
