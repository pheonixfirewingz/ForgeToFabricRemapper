package com.minecolonies.coremod.colony.colony.buildings.workerbuildings;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;


/**
 * Class of the stone smeltery building.
 */
public class BuildingStoneSmeltery extends AbstractBuildingSmelterCrafter
{
    /**
     * Description string of the building.
     */
    private static final String STONE_SMELTERY = "stonesmeltery";

    /**
     * Instantiates a new stone smeltery building.
     *
     * @param c the colony.
     * @param l the location
     */
    public BuildingStoneSmeltery(final IColony c, final BlockPos l)
    {
        super(c, l);
    }

    @NotNull
    @Override
    public String getSchematicName()
    {
        return STONE_SMELTERY;
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
        return new JobStoneSmeltery(citizen);
    }

    @NotNull
    @Override
    public String getJobName()
    {
        return STONE_SMELTERY;
    }

    @NotNull
    @Override
    public Skill getPrimarySkill()
    {
        return Skill.Athletics;
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
        if (!super.canRecipeBeAdded(token))
        {
            return false;
        }

        final IRecipeStorage storage = IColonyManager.getInstance().getRecipeManager().getRecipes().get(token);
        if (storage == null)
        {
            return false;
        }

        if (storage.getIntermediate() != Blocks.FURNACE || storage.getInput().isEmpty())
        {
            return false;
        }

        Optional<Boolean> isRecipeAllowed;

        isRecipeAllowed = super.canRecipeBeAddedBasedOnTags(token);
        
        return isRecipeAllowed.orElse(false);
    }

    @Override
    public BuildingEntry getBuildingRegistryEntry()
    {
        return ModBuildings.stoneSmelter;
    }

    @Override
    public void requestUpgrade(final PlayerEntity player, final BlockPos builder)
    {
        final UnlockBuildingResearchEffect effect = colony.getResearchManager().getResearchEffects().getEffect(ResearchInitializer.STONESMELTERY_RESEARCH, UnlockBuildingResearchEffect.class);
        if (effect == null)
        {
            player.sendMessage(new TranslationTextComponent("com.minecolonies.coremod.research.havetounlock"), player.getUniqueID());
            return;
        }
        super.requestUpgrade(player, builder);
    }

    /**
     * Stone smeltery View.
     */
    public static class View extends AbstractBuildingSmelterCrafter.View
    {

        /**
         * Instantiate the stone smeltery view.
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
            return new WindowHutStoneSmelter(this);
        }
    }
}
