package com.minecolonies.coremod.colony.colony.buildings.workerbuildings;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;


/**
 * Class of the fletcher building.
 */
public class BuildingFletcher extends AbstractBuildingCrafter
{
    /**
     * Description string of the building.
     */
    private static final String FLETCHER = "fletcher";

    /**
     * Instantiates a new fletcher building.
     *
     * @param c the colony.
     * @param l the location
     */
    public BuildingFletcher(final IColony c, final BlockPos l)
    {
        super(c, l);
    }

    @NotNull
    @Override
    public String getSchematicName()
    {
        return FLETCHER;
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
        return new JobFletcher(citizen);
    }

    @NotNull
    @Override
    public String getJobName()
    {
        return FLETCHER;
    }

    @NotNull
    @Override
    public Skill getPrimarySkill()
    {
        return Skill.Dexterity;
    }

    @NotNull
    @Override
    public Skill getSecondarySkill()
    {
        return Skill.Creativity;
    }

    @Override
    @NotNull
    public Skill getCraftSpeedSkill()
    {
        return getPrimarySkill();
    }

    @Override
    @NotNull
    public Skill getRecipeImprovementSkill()
    {
        return getSecondarySkill();
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

            return storage.getPrimaryOutput().getItem() instanceof ArrowItem
                     || (storage.getPrimaryOutput().getItem() instanceof DyeableArmorItem
                           && ((DyeableArmorItem) storage.getPrimaryOutput().getItem()).getArmorMaterial() == ArmorMaterial.LEATHER);

            // End Additional recipe rules
        }
    }

    @Override
    public BuildingEntry getBuildingRegistryEntry()
    {
        return ModBuildings.fletcher;
    }

    @Override
    public void requestUpgrade(final PlayerEntity player, final BlockPos builder)
    {
        final UnlockBuildingResearchEffect effect = colony.getResearchManager().getResearchEffects().getEffect(ResearchInitializer.FLETCHER_RESEARCH, UnlockBuildingResearchEffect.class);
        if (effect == null)
        {
            player.sendMessage(new TranslationTextComponent("com.minecolonies.coremod.research.havetounlock"), player.getUniqueID());
            return;
        }
        super.requestUpgrade(player, builder);
    }

    /**
     * Fletcher View.
     */
    public static class View extends AbstractBuildingCrafter.View
    {

        /**
         * Instantiate the fletcher view.
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
            return new WindowHutCrafter(this, FLETCHER);
        }
    }
}