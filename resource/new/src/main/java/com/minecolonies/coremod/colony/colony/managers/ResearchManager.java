package com.minecolonies.coremod.colony.colony.managers;

import org.jetbrains.annotations.NotNull;

/**
 * Research manager of the colony.
 */
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
