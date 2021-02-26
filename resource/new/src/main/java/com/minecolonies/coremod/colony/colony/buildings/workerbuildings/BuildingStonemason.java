package com.minecolonies.coremod.colony.colony.buildings.workerbuildings;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;


/**
 * Class of the stonemason building.
 */
public class BuildingStonemason extends AbstractBuildingCrafter
{
    /**
     * Description string of the building.
     */
    private static final String STONEMASON = "stonemason";

    /**
     * The min percentage something has to have out of stone to be craftable by this worker.
     */
    private static final double MIN_PERCENTAGE_TO_CRAFT = 0.75;

    /**
     * Instantiates a new stonemason building.
     *
     * @param c the colony.
     * @param l the location
     */
    public BuildingStonemason(final IColony c, final BlockPos l)
    {
        super(c, l);
    }

    @NotNull
    @Override
    public String getSchematicName()
    {
        return STONEMASON;
    }

    @Override
    public int getMaxBuildingLevel()
    {
        return CONST_DEFAULT_MAX_BUILDING_LEVEL;
    }

    @NotNull
    @Override
    public IJob<?> createJob(final ICitizenData citizen)
    {
        return new JobStonemason(citizen);
    }

    @NotNull
    @Override
    public String getJobName()
    {
        return STONEMASON;
    }

    @NotNull
    @Override
    public Skill getPrimarySkill()
    {
        return Skill.Creativity;
    }

    @NotNull
    @Override
    public Skill getSecondarySkill()
    {
        return Skill.Dexterity;
    }

    @Override
    public boolean canRecipeBeAdded(final IToken<?> token)
    {

        Optional<Boolean> isRecipeAllowed;

        if (!super.canRecipeBeAdded(token))
        {
            return false;
        }

        isRecipeAllowed = super.canRecipeBeAddedBasedOnTags(token);
        if (isRecipeAllowed.isPresent())
        {
            return isRecipeAllowed.get();
        }
        else
        {
            // Additional recipe rules

            final IRecipeStorage storage = IColonyManager.getInstance().getRecipeManager().getRecipes().get(token);

            // End Additional recipe rules
        }

        return false;
    }

    @Override
    public BuildingEntry getBuildingRegistryEntry()
    {
        return ModBuildings.stoneMason;
    }

    @Override
    public void requestUpgrade(final PlayerEntity player, final BlockPos builder)
    {
        final UnlockBuildingResearchEffect effect = colony.getResearchManager().getResearchEffects().getEffect(ResearchInitializer.STONEMASON_RESEARCH, UnlockBuildingResearchEffect.class);
        if (effect == null)
        {
            player.sendMessage(new TranslationTextComponent("com.minecolonies.coremod.research.havetounlock"), player.getUniqueID());
            return;
        }
        super.requestUpgrade(player, builder);
    }

    /**
     * Crafter building View.
     */
    public static class View extends AbstractBuildingCrafter.View
    {

        /**
         * Instantiate the stonemason view.
         *
         * @param c the colonyview to put it in
         * @param l the positon
         */
        public View(final IColonyView c, final BlockPos l)
        {
            super(c, l);
        }

        @NotNull
        @Override
        public Window getWindow()
        {
            return new WindowHutCrafter(this, STONEMASON);
        }
    }
}
