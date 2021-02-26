package com.minecolonies.api.crafting;

import com.google.common.collect.ImmutableList;
import net.minecraft.item.*;
import net.minecraft.util.*;

import java.util.*;

/**
 * The mult-output recipe type
 */
public class MultiOutputRecipe extends AbstractRecipeType<IRecipeStorage>
{
    /**
     * Cache of item stacks for display
     */
    private final ArrayList<ItemStack> outputDisplayStacks = new ArrayList<>();

    /**
     * Multi-Output recipe type
     */
    public MultiOutputRecipe(IRecipeStorage recipe)
    {
        super(recipe);
    }

    @Override
    public List<ItemStack> getOutputDisplayStacks()
    {
        if(outputDisplayStacks.isEmpty())
        {
            outputDisplayStacks.add(recipe.getPrimaryOutput());
            outputDisplayStacks.addAll(recipe.getAlternateOutputs());
        }
        return ImmutableList.copyOf(outputDisplayStacks);
    }

    @Override
    public ResourceLocation getId()
    {
        return ModRecipeTypes.MULTI_OUTPUT_ID;
    }
}
