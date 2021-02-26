package com.minecolonies.coremod.colony.colony.buildings.workerbuildings;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;


/**
 * Class of the mechanic building.
 */
public class BuildingMechanic extends AbstractBuildingCrafter
{
    /**
     * Description string of the building.
     */
    private static final String MECHANIC = "mechanic";

    /**
     * Instantiates a new mechanic building.
     *
     * @param c the colony.
     * @param l the location
     */
    public BuildingMechanic(final IColony c, final BlockPos l)
    {
        super(c, l);
    }

    @NotNull
    @Override
    public String getSchematicName()
    {
        return MECHANIC;
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
        return new JobMechanic(citizen);
    }

    @NotNull
    @Override
    public String getJobName()
    {
        return MECHANIC;
    }

    @NotNull
    @Override
    public Skill getPrimarySkill()
    {
        return Skill.Knowledge;
    }

    @NotNull
    @Override
    public Skill getSecondarySkill()
    {
        return Skill.Agility;
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

            return storage.getPrimaryOutput().getItem() instanceof MinecartItem
                    || (storage.getPrimaryOutput().getItem() instanceof BlockItem && ((BlockItem) storage.getPrimaryOutput().getItem()).getBlock() instanceof HopperBlock);
            // End Additional recipe rules
        }
    }

    @Override
    public BuildingEntry getBuildingRegistryEntry()
    {
        return ModBuildings.mechanic;
    }

    @Override
    public void requestUpgrade(final PlayerEntity player, final BlockPos builder)
    {
        final UnlockBuildingResearchEffect effect = colony.getResearchManager().getResearchEffects().getEffect(ResearchInitializer.MECHANIC_RESEARCH, UnlockBuildingResearchEffect.class);
        if (effect == null)
        {
            player.sendMessage(new TranslationTextComponent("com.minecolonies.coremod.research.havetounlock"), player.getUniqueID());
            return;
        }
        super.requestUpgrade(player, builder);
    }

    /**
     * Mechanic View.
     */
    public static class View extends AbstractBuildingCrafter.View
    {

        /**
         * Instantiate the mechanic view.
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
            return new WindowHutCrafter(this, MECHANIC);
        }
    }
}
