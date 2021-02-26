package com.minecolonies.coremod.colony.colony.managers;

import com.minecolonies.api.research.IResearchManager;
import com.minecolonies.api.research.effects.IResearchEffectManager;
import com.minecolonies.coremod.research.LocalResearchTree;
import com.minecolonies.coremod.research.ResearchEffectManager;
import net.minecraft.nbt.CompoundNBT;
import org.jetbrains.annotations.NotNull;

/**
 * Research manager of the colony.
 */
public class ResearchManager
{
    /**
     * The research tree of the colony.
     */
    private final LocalResearchTree tree = new LocalResearchTree();

    /**
     * The research effects of the colony.
     */
    private final IResearchEffectManager effects = new ResearchEffectManager();

    
    public void readFromNBT(@NotNull final CompoundNBT compound)
    {
        tree.readFromNBT(compound, effects);
    }

    
    public void writeToNBT(@NotNull final CompoundNBT compound)
    {
        tree.writeToNBT(compound);
    }

    
    public LocalResearchTree getResearchTree()
    {
        return this.tree;
    }

    
    public IResearchEffectManager getResearchEffects()
    {
        return this.effects;
    }
}
