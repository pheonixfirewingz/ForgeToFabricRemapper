package com.minecolonies.coremod.colony.colony.jobs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * The Lumberjack job class.
 */
public class JobLumberjack extends AbstractJobCrafter<EntityAIWorkLumberjack, JobLumberjack>
{
    /**
     * Walking speed bonus per level
     */
    public static final double BONUS_SPEED_PER_LEVEL = 0.003;

    /**
     * The tree this lumberjack is currently working on.
     */
    @Nullable
    private Tree tree;

    /**
     * Create a lumberjack job.
     *
     * @param entity the lumberjack.
     */
    public JobLumberjack(final ICitizenData entity)
    {
        super(entity);
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        final CompoundNBT compound = super.serializeNBT();
        @NotNull final CompoundNBT treeTag = new CompoundNBT();

        if (tree != null)
        {
            tree.write(treeTag);
        }

        compound.put(TAG_TREE, treeTag);
        return compound;
    }

    @Override
    public JobEntry getJobRegistryEntry()
    {
        return ModJobs.lumberjack;
    }

    @NotNull
    @Override
    public String getName()
    {
        return "com.minecolonies.coremod.job.Lumberjack";
    }

    @NotNull
    @Override
    public BipedModelType getModel()
    {
        return BipedModelType.LUMBERJACK;
    }

    @Override
    public void deserializeNBT(final CompoundNBT compound)
    {
        super.deserializeNBT(compound);
        if (compound.keySet().contains(TAG_TREE))
        {
            tree = Tree.read(compound.getCompound(TAG_TREE));
            if (!tree.isTree())
            {
                tree = null;
            }
        }
    }

    @Override
    public void onLevelUp()
    {
        if (getCitizen().getEntity().isPresent())
        {
            final AbstractEntityCitizen worker = getCitizen().getEntity().get();
            final AttributeModifier speedModifier = new AttributeModifier(SKILL_BONUS_ADD, (getCitizen().getCitizenSkillHandler().getLevel(getCitizen().getWorkBuilding().getSecondarySkill()) / 2.0 ) * BONUS_SPEED_PER_LEVEL, AttributeModifier.Operation.ADDITION);
            AttributeModifierUtils.addModifier(worker, speedModifier, Attributes.MOVEMENT_SPEED);
        }
    }
    
    /**
     * Get the current tree the lumberjack is cutting.
     *
     * @return the tree.
     */
    @Nullable
    public Tree getTree()
    {
        return tree;
    }

    /**
     * Set the tree he is currently cutting.
     *
     * @param tree the tree.
     */
    public void setTree(@Nullable final Tree tree)
    {
        this.tree = tree;
    }

    @NotNull
    @Override
    public EntityAIWorkLumberjack generateAI()
    {
        return new EntityAIWorkLumberjack(this);
    }
}
